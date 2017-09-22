# Packet Protocol Library (PPL)
PPL is a lightweight network library written in Java. 

## Packets
PPL is presupposed around packets. A packet is any ecapsulation of data. To define a packet:

    public class Packet1 extends io.github.alivety.ppl.Packet {
        @io.github.alivety.ppl.PacketField private final int id = 0; // this must be set correctly
	    @io.github.alivety.ppl.PacketField public String username;
	    @io.github.alivety.ppl.PacketField public int protocolVersion;
    }

Any data that you wish to transmit, use a packet to capture that data. While PPL does support raw data, it is not officially condoned.

## Networking
PPL offers `PPLServer` and `PPLClient` for servers and clients respectively (obviously). Neither take any arguments to be initialized. What they do use is chained methods:

    PPLServer s = new PPLServer().addSocketListener(new SocketListener(){...}).bind(3030);
    PPLClient c = new PPLCLient().addSocketListener(new SocketListener(){...}).connect("localhost",3030);
 
There are no further (public) methods for either networking class. They will automagically wait for all the data of a packet to be received, transform raw network data into packets, and fire read events on the socket listener whenever an entire packet is read.

## SockerListener
A `SocketListener` has three methods: `connect(SocketChannel ch)`, `read(SocketChannel ch, ByteBuffer msg)`, and `exception(SocketChannel ch, Throwable t)`. `SocketChannel` and `ByteBuffer` both are the Java NIO classes, not an abstraction.

`connect` is called for servers when a new client connects and for clients when it connects to the server.

`read` is called for servers when a client sends a packet for for clients when the server sends a packet.

`exception` is called for both whenever an exception is thrown by PPL. 
