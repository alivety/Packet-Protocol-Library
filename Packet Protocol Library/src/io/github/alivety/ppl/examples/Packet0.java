package io.github.alivety.ppl.examples;

import io.github.alivety.ppl.AbstractPacket;
import io.github.alivety.ppl.PacketField;

public class Packet0 extends AbstractPacket {
	@PacketField
	private final int id=0;
	@PacketField
	private String call;
}
