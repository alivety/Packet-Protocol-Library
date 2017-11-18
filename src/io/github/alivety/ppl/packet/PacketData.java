package io.github.alivety.ppl.packet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PacketData {
	int id();
	
	String desc() default "N/A";
	
	Class<?> bound() default Common.class;
}