# Copyright 2011 James McCauley
#
# This file is part of POX.
#
# POX is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# POX is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with POX.  If not, see <http://www.gnu.org/licenses/>.

"""
A stupid L3 switch

For each switch:
1) Keep a table that maps IP addresses to MAC addresses and switch ports.
   Stock this table using information from ARP and IP packets.
2) When you see an ARP query, try to answer it using information in the table
   from step 1.  If the info in the table is old, just flood the query.
3) Flood all other ARPs.
4) When you see an IP packet, if you know the destination port (because it's
   in the table from step 1), install a flow for it.
"""

from pox.core import core
import pox
import pdb
log = core.getLogger()

from pox.lib.addresses import *
from pox.lib.packet.ethernet import ethernet
from pox.lib.packet.ipv4 import ipv4
from pox.lib.packet.arp import arp
from pox.lib.packet.ethernet import ETHER_BROADCAST

import pox.openflow.libopenflow_01 as of

from pox.lib.revent import *

import time

# Timeout for flows
FLOW_IDLE_TIMEOUT = 10

# Timeout for ARP entries
ARP_TIMEOUT = 60 * 2

# Global unsolved IP List_with content tuple(dst_IP,dpid,buffer_ID,inport,src_IP)__fgong
TEST            = 1
UNKNOWN_IP_LIST = []
CONTENTS        = []    		     #Global Content ID List
#SWITCH_IP       = ['10.10.1.2', '10.10.4.1'] #Hard-coded switch IPs

class Entry (object):
  """
  Not strictly an ARP entry.
  We use the port to determine which port to forward traffic out of.
  We use the MAC to answer ARP replies.
  We use the timeout so that if an entry is older than ARP_TIMEOUT, we
   flood the ARP request rather than try to answer it ourselves.
  """
  def __init__ (self, port, mac):
    self.timeout = time.time() + ARP_TIMEOUT
    self.port = port
    self.mac = mac

  def __eq__ (self, other):
    if type(other) == tuple:
      return (self.port,self.mac)==other
    else:
      return (self.port,self.mac)==(other.port,other.mac)
  def __ne__ (self, other):
    return not self.__eq__(other)

  def isExpired (self):
    return time.time() > self.timeout


class l3_switch (EventMixin):
  def __init__ (self):
    # For each switch, we map IP addresses to Entries
    self.arpTable = {}

    self.listenTo(core)

  def _handle_GoingUpEvent (self, event):
    self.listenTo(core.openflow)
    log.debug("Up...")

  def _handle_PacketIn (self, event):
    global TEST
    global EVENT
    log.info("GLOBAL I = %d", TEST)
    TEST = TEST + 1
    dpid = event.connection.dpid
    inport = event.port
    packet = event.parsed
    if not packet.parsed:
      log.warning("%i %i ignoring unparsed packet", dpid, inport)
      return

    if dpid not in self.arpTable:
      # New switch -- create an empty table
      self.arpTable[dpid] = {}

    if packet.type == ethernet.LLDP_TYPE:
      # Ignore LLDP packets
      return

    if isinstance(packet.next, ipv4):
      log.debug("%i %i IP %s => %s", dpid,inport,str(packet.next.srcip),str(packet.next.dstip))
      
      # Find out if the incoming packet is UDP packet. 
      udp = packet.find('udp')
      if udp is not None:
	#pdb.set_trace() #udp.next is the transmitted UDP string packet.
	log.debug("UDP packet coming...")
	content_ID = udp.next
	CONTENTS.append(content_ID)
	#return
      """      
      # Find out if the packet is for the switches
      if packet.next.dstip in SWITCH_IP:
	return
      """
      # Learn or update port/MAC info
      if packet.next.srcip in self.arpTable[dpid]:
        if self.arpTable[dpid][packet.next.srcip] != (inport, packet.src):
          log.info("%i %i RE-learned %s", dpid,inport,str(packet.next.srcip))
      else:
        log.debug("%i %i learned %s", dpid,inport,str(packet.next.srcip))
      self.arpTable[dpid][packet.next.srcip] = Entry(inport, packet.src)

      # Try to forward
      dstaddr = packet.next.dstip
      if dstaddr in self.arpTable[dpid]:
        # We have info about what port to send it out on...

        prt = self.arpTable[dpid][dstaddr].port
	dst_MAC = self.arpTable[dpid][dstaddr].mac
        if prt == inport:
          log.warning("%i %i not sending packet for %s back out of the input port" % (
           dpid, inport, str(dstaddr)))
        else:
          log.debug("%i %i installing flow for %s => %s out port %i" % (dpid,
            inport, str(packet.next.srcip), str(dstaddr), prt))
	  #Create the Matching and flow modification message_fgong
	  msg = of.ofp_flow_mod()
	  msg.command = of.OFPFC_ADD
	  msg.idle_timeout  = FLOW_IDLE_TIMEOUT
	  msg.hard_timeout  = of.OFP_FLOW_PERMANENT
	  msg.buffer_id     = event.ofp.buffer_id
	  msg.match.dl_type = 0x800 #IPv4 packets 
	  msg.match.nw_dst  = IPAddr(str(dstaddr)) #Matching the destination address
	  msg.match.in_port = inport
	  if packet.dst != dst_MAC: #Change DST_MAC
	    msg.actions.append(of.ofp_action_dl_addr.set_dst(EthAddr(dst_MAC.toStr())))
	    log.debug("MAC changed from %s to %s", packet.dst.toStr(), dst_MAC.toStr())	  
	  msg.actions.append(of.ofp_action_output(port = prt))      #Forward to out_port
          event.connection.send(msg.pack())
      # Added by fgong for handling unknown IP packets through flooding ARP..but not working yet.
      else:
        a = packet.next
        log.debug("%i %i Unknown destination IP Packet %s", dpid, inport, str(a.dstip))
       	#If the destination IP is not given in the table, an ARP should be broadcasted. 
       
	log.debug("Constructing OWN ARP broadcast request for %s", str(a.dstip))
        ARP_TYPE  = 0x0806	
        r = arp()
        r.opcode = arp.REQUEST
        r.hwsrc = EthAddr(packet.src)
        r.protosrc = a.srcip
        r.protodst = a.dstip
        r.hwdst = ETHER_BROADCAST 
        e = ethernet(type=ARP_TYPE, src=r.hwsrc, dst=r.hwdst)
        e.set_payload(r)
        log.debug("%i %i broadcasting constructed ARP for %s" % (dpid, inport, str(r.protodst)))
        msg = of.ofp_packet_out()
        msg.data = e.pack()
        msg.actions.append(of.ofp_action_output(port = of.OFPP_FLOOD))
        msg.in_port = inport
        event.connection.send(msg)
        #event.connection.send(msg.pack()) #Differece on msy.pack()?
        log.debug("unknown IP Packet's ARP flooded, Waiting for ARP found...")
	UNKNOWN_IP_LIST.append((a.dstip, dpid, event.ofp.buffer_id, inport, a.srcip))
	#No idea how to handle this IP packet, waiting for the ARP reply by caching the request...
    #End adding by fgong
    elif isinstance(packet.next, arp):
      a = packet.next
      log.debug("%i %i ARP %s %s => %s", dpid, inport,
       {arp.REQUEST:"request",arp.REPLY:"reply"}.get(a.opcode,
       'op:%i' % (a.opcode,)), str(a.protosrc), str(a.protodst))

      if a.prototype == arp.PROTO_TYPE_IP:
        if a.hwtype == arp.HW_TYPE_ETHERNET:
          if a.protosrc != 0:

            # Learn or update port/MAC info
            if a.protosrc in self.arpTable[dpid]:
              if self.arpTable[dpid][a.protosrc] != (inport, packet.src):
                log.info("%i %i RE-learned %s", dpid,inport,str(a.protosrc))
            else:
              log.debug("%i %i learned %s", dpid,inport,str(a.protosrc))
            self.arpTable[dpid][a.protosrc] = Entry(inport, packet.src)
            # Check current table if it has a knowledge of the UNKNOWN IP's MAC info.
            while len(UNKNOWN_IP_LIST)!=0 :
	      cache = UNKNOWN_IP_LIST.pop()
	      cache_IP = cache[0]
	      cache_dpid = cache[1]
	      cache_bufferID = cache[2]
	      cache_inport = cache[3]
	      cache_src_Ip = cache[4]
	      if (cache_dpid == dpid) and (cache_IP in self.arpTable[dpid]):
		prt = self.arpTable[dpid][cache_IP].port
	        dst_MAC = self.arpTable[dpid][cache_IP].mac
		log.debug("Have an idea how to handle the previous unsolved IP packet...")
	        log.debug("%i %i installing flow for %s => %s out port %i" % (dpid, cache_inport, str(cache_src_Ip), str(cache_IP), prt))
	  	#Create the Matching and flow modification message_fgong
	  	msg = of.ofp_flow_mod()
	  	msg.command = of.OFPFC_ADD
	  	msg.idle_timeout  = FLOW_IDLE_TIMEOUT
	  	msg.hard_timeout  = of.OFP_FLOW_PERMANENT
	  	msg.buffer_id     = cache_bufferID
		msg.match.dl_type = 0x800 #IPv4 packets 
	  	msg.match.nw_dst  = IPAddr(str(cache_IP)) #Matching the destination address
	  	msg.match.in_port = cache_inport
	      	msg.actions.append(of.ofp_action_dl_addr.set_dst(EthAddr(dst_MAC.toStr())))
	    	log.debug("MAC changed to %s", dst_MAC.toStr())	
                msg.actions.append(of.ofp_action_output(port = prt))      #Forward to out_port
	 	event.connection.send(msg.pack())
	      else : 
		UNKNOWN_IP_LIST.append(cache)
		break
	
            if a.opcode == arp.REQUEST:
              # Maybe we can answer

              if a.protodst in self.arpTable[dpid]:
                # We have an answer...

                if not self.arpTable[dpid][a.protodst].isExpired():
                  # .. and it's relatively current, so we'll reply ourselves

                  r = arp()
                  r.hwtype = a.hwtype
                  r.prototype = a.prototype
                  r.hwlen = a.hwlen
                  r.protolen = a.protolen
                  r.opcode = arp.REPLY
                  r.hwdst = a.hwsrc
                  r.protodst = a.protosrc
                  r.protosrc = a.protodst
                  r.hwsrc = self.arpTable[dpid][a.protodst].mac
                  e = ethernet(type=packet.type, src=r.hwsrc, dst=a.hwsrc)
                  e.set_payload(r)
                  log.debug("%i %i answering ARP for %s" % (dpid, inport,
                   str(r.protosrc)))
                  msg = of.ofp_packet_out()
                  msg.data = e.pack()
                  msg.actions.append(of.ofp_action_output(port =
                                                          of.OFPP_IN_PORT))
                  msg.in_port = inport
                  event.connection.send(msg)
                  return

      # Didn't know how to answer or otherwise handle this ARP, so just flood it
      log.debug("%i %i flooding ARP %s %s => %s" % (dpid, inport,
       {arp.REQUEST:"request",arp.REPLY:"reply"}.get(a.opcode,
       'op:%i' % (a.opcode,)), str(a.protosrc), str(a.protodst)))

      msg = of.ofp_packet_out(in_port = inport, action = of.ofp_action_output(port = of.OFPP_FLOOD))
      if event.ofp.buffer_id is of.NO_BUFFER:
        # Try sending the (probably incomplete) raw data
        msg.data = event.data
      else:
        msg.buffer_id = event.ofp.buffer_id
      event.connection.send(msg.pack())

    return


def launch ():
  core.registerNew(l3_switch)

