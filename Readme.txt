Client:
1)sends URL to Switch and waits
2)checks what reply it gets from switch
if it is "Switch",it implements coded "wget switch address"
else 
it downloads from cache VM address

Switch:
1)forwards URL to controller
2)if it gets "No" from controller,implements "wget URL" on itself
else
sends command to host to download from cache VM

Controller:
1)Receives URL from switch
2)Checks in cache table
3)if entry in cache table,increments counter by 1
    & sends to switch "Yes"
4)if no entry,makes entry nd applies counter=1 then
a)sends "No " to Switch
b)sends "URL" to cache Switch which gets forwarded to cache VM which applies wget and downloads it.

Cache Switch
Just forwards URL from controller to cache VM

CacheVM
Receives URL and downloads it locally.


Files:

UDP Server is for controller
UDPClient is for host
UDPRelay is for normal Switch
