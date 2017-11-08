# Packet Protocol Library (PPL)
The Packet Protocol Library is a lightweight networking library written in Java. It is designed around Java NIO, removing all the boilerplate and creating the Packet abstraction of data communication so that the infrastructure for networking is already in place.

PPL was born out of my many frustrations with Netty and pure Java NIO. I wanted a bare-bones abstraction of both.

## The Packet

The Packet is the basic unit of data for PPL. A Packet is simply any class which:
* Extends the `Packet` class; and
* Is annotated with `PacketData`

Any number of fields may appear inside a packet. For example,

    @PacketData(id=0)
    public class PacketColor extends Packet {
       public @PacketField java.awt.Color color;
       public @PacketField int random;
    }

The `PacketData` annotation has three fields that you may set:
* (int) id: This is the only required field, any must be unique for each packet in your program.
* (String) desc: A descripton of what the packet does, e.g. what each field should contain.
* (Class<?>) bound: `Clientside.class`, `Serverside.class`, or `Common.class`.

Any packet may access this information with its `getData()` method. "desc" and "bound" default to "N/A" and `Common.class` by default.

## Network

`PPLClient` and `PPLServer` are the classes by which Java NIO is wrapped.
