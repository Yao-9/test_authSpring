package test_AuthSpring;

import static org.junit.Assert.*;

import java.io.IOException;

import SpringAuth.*;

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
		accCode = "4/JtJDCzqNhQD5V228pb72IDxTZCVmH65Y56Kp__DNvo0.0nG5sKV3sq4XXmXvfARQvtjoshpImAI";
		pkt = new AccessCodePacket(accCode, "123", "456");
	}

	@Test
	public void test_getCredential() throws IOException {
		assertTrue(c.authCodeHandler(pkt).equals("Success"));
	}
}
