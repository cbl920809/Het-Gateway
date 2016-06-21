#Format Raspberry Pi SD card rapidly
Format Tools  
NOOBS (New Out Of Box Software) is an easy operating system install manager for the Raspberry Pi.
# Het-Gateway
Base on OM2M M2M Service Platform Het-Gateway

#OM2M Website
http://www.eclipse.org/om2m/

#Bluetooth Setup
Setup Bluetooth package and pair with another device.
This steps try to connect a bluetooth device.
$ sudo -s
$ apt-get install bluez python-gobject
$ hcitool dev
$ hcitool scan
$ echo 0000 | sudo bluez-simple-agent hci0 00:0F:F6:82:D1:BB
$ sudo bluez-test-device trusted 00:0F:F6:82:D1:BB yes
$ sudo bluez-test-input connect 00:0F:F6:82:D1:BB 
$ hcitool con

#R Pi Wifi Hotspot Setup
1. Installed packages and its dependency 
$ sudo apt-get install hostapd hostap-utils dnsmasq iw bridge-utils
  hostapd: the hostap wireless access point daemon
  hostap-utils: supplemental hostap tools
  iw: wireless configuration utility
  dnsmasq: DHCP and DNS utility
  bridge-utils:  used for connecting multiple Ethernet devices together
NOTE:
  Run “sudo apt-get update” if some packages were Not Found.

2. Replace hostapd hostapd-cli (See Appendix E. Compile hostapd)

3. Change network setting at /etc/network/interfaces
$ sudo vi /etc/network/interfaces
auto lo
iface lo inet loopback

allow-hotplug eth0
iface eth0 inet static
address 140.114.XXX.YYY
netmask 255.255.255.0
gateway 140.114.XXX.254 

allow-hotplug wlan0
iface wlan0 inet manual

4. Create hostapd.conf
See: http://w1.fi/cgit/hostap/plain/hostapd/hostapd.conf
$ sudo vi /etc/hostapd/hostapd.conf

interface=wlan0
bridge=br0
driver=rtl871xdrv
ctrl_interface=/var/run/hostapd

ssid=RaspGW
country_code=TW
hw_mode=g
channel=6
beacon_int=100
dtim_period=2
auth_algs=1
ignore_broadcast_ssid=0

ieee80211n=1
wmm_enabled=1
ht_capab=[HT40+][SHORT-GI-40][DSSS_CCK-40]

wpa=2
wpa_passphrase=mtc5g34182
wpa_key_mgmt=WPA-PSK
wpa_pairwise=TKIP
rsn_pairwise=CCMP

$ sudo vi /etc/default/hostapd
Find the line #DAEMON_CONF="" and edit it so it says
DAEMON_CONF="/etc/hostapd/hostapd.conf"

5. Uncomment the following lines in /etc/dnsmasq.conf and adjust them to your environment:
domain-needed
interface=br0
dhcp-range=192.168.1.50,192.168.1.150,12h
server=8.8.8.8

6. Enable forwarding to reach Internet. Mask for the interface, activate port-forwarding and NAT
$ iptables -t nat -A POSTROUTING -s 192.168..0/24(??) -j MASQUERADE

$ sysctl -w net.ipv4.ip_forward=1

$ sudo /etc/init.d/hostapd restart
$ sudo /etc/init.d/dnsmasq restart

7. Default enable ipv4 forwarding
$ sudo vi /etc/sysctl.conf
$ uncomment :
net.ipv4.ip_forward=1

8. ssh tunnel setting
$ ssh -NfR 8181:127.0.0.1:8181 ubuntu@140.114.91.204
$ sudo vi /etc/ssh/ssh_config
ServerAliveInterval 60
$ sudo service ssh restart

9. Change hostname
$ hostname RaspGW2
$ sudo vi /etc/hosts
$ 127.0.1.1  	raspberrypi
$ 127.0.1.1   	RaspGW2

10. Prevent slow login
$sudo vi /etc/ssh/sshd_config
     $ disable DNS lookup when login
	UseDNS no
$sudo service ssh restart

11. Auto start hostapd
$vi /etc/rc.local
$if use openvswitch
hostname RaspGW1
ifconfig br0 192.168.3.1 netmask 255.255.255.0
/etc/init.d/hostapd restart
/etc/init.d/dnsmasq restart
iptables -t nat -A POSTROUTING -s 192.168.3.0/24 -j MASQUERADE

$if use linux bridge
hostname Rasp2GW1
brctl addbr br0
ifconfig br0 192.168.3.1 netmask 255.255.255.0
/etc/init.d/hostapd restart
/etc/init.d/dnsmasq restart
iptables -t nat -A POSTROUTING -s 192.168.3.0/24 -j MASQUERADE
brctl addif br0 wlan0

