package SpringAuth;

import java.io.IOException;

import org.springframework.web.bind.annotation.*;

import packet.AccessCodePacket;

import com.google.api.client.auth.oauth2.Credential;

@RestController
public class Controller {
	private GoogleDriveAPI gda = new GoogleDriveAPI();
	
	/* Receive the Token From User, Store it to HashTable */
	@RequestMapping("/auth")
	@ResponseBody
	public String authCodeHandler(@RequestBody AccessCodePacket pkt) throws IOException {
		String accCode = pkt.getAccessCode();
		Credential cred = null;
		if(gda.getConfidentialByAccCode(accCode))
			return "Success";
		else
			return "Failure";
	}
}
