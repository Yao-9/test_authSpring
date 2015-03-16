package test_AuthSpring;

import static org.junit.Assert.*;
import SpringAuth.*;

import java.util.ResourceBundle.Control;

import org.junit.Before;
import org.junit.Test;

import packet.AccessCodePacket;

public class Test_auth {
	Controller c = null;
	String accCode = null;
	AccessCodePacket pkt;
	
	@Before
	public void setUp() throws Exception {
		this.c = new Controller();
		accCode = "4/nNePJDTohSp0bCyL8n7a615P2BKwBOhJGiU4GTs3yOA.EvSoXE00ycEaXmXvfARQvtg2a9g9mAI";
		pkt = new AccessCodePacket(accCode, "123", "456");
	}

	@Test
	public void test_getCredential() {
		assertTrue(c.authCodeHandler(pkt).equals("Success"));
	}


}
