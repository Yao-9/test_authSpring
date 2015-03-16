package SpringAuth;

import org.springframework.web.bind.annotation.*;
import packet.AccessCodePacket;
import com.google.api.client.auth.oauth2.Credential;

@RestController
public class Controller {

	/* Receive the Token From User, Store it to HashTable */
	@RequestMapping("/auth")
	@ResponseBody
	public String authCodeHandler(@RequestBody AccessCodePacket pkt) {
		String accCode = pkt.getAccessCode();
		Credential cred = null;
		try {
			cred = GoogleDriveAPI.getCredentials(accCode, "");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (cred != null) {
			System.out.print("Credential Success :) \n");
			return "Success";
		} else {
			System.out.print("Credential Fail :( \n");
			return "Fail";
		}
	}
}
