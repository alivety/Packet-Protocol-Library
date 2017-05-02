package io.github.alivety.ppl.examples;

import io.github.alivety.ppl.AbstractPacket;
import io.github.alivety.ppl.PacketField;

public class Packet1 extends AbstractPacket {
	@PacketField
	private final int id=1;
	@PacketField
	private String response;
}
