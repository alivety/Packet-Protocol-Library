# Packet Protocol Library (PPL)
PPL is a lightweight network library written in Java. I designed it to be like Netty, but less verbose and more tailored for my specific needs.
It works by encapsulating all data in developer-defined packets (although it is possible to send raw data). Defining a packet is easy:

    package example;
    class Packet0 extends AbstractPacket {
      @PacketField
      private final int id=0;
      @PacketField
      private String[] array;
      @PacketField
      private int number;
    }
    
Take notice that packet classes must be named as "Packet[packet id]".

To create an instance of this Packet0:

    AbstractPacket.c(Packet0.class, new String[]{"hi","u","ok"}, 7);
    
Each argument after the packet class corresponds to a declared packet field, in the order that they were declared.

Now to read and write this packet:

    public void read(SocketChannel ch,ByteBuffer msg) {
      AbstractPacket p = AbstractPacket.decode("example.Packet",msg);
      if (p.getPacketID()==0) {
        String[] array=p.getField("array");
        int number=p.getField("number");
      }
      AbstractPacket rsp=AbstractPacket.c(Packet0.class, new String[]{"hi","u","ok"}, 7);
      ch.write(PPL.encapsulate(rsp.encode()));
    }
    
This code snippet is pretty self-explanatory up to the last line. `PPL.encapsulate(ByteBuffer)` prepends the length of a ByteBuffer to it.
This is used when PPL automagically waits until the entire packet is read to trigger `SocketListener.read()`. 

Take a look at [the examples](https://github.com/alivety/Packet-Protocol-Library/tree/21e4c2f79829c046dfe78ed043c9fd6bf14ea0d3/Packet%20Protocol%20Library/src/io/github/alivety/ppl/examples) for a full implementation.
