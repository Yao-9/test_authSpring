package SpringAuth;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

public class GoogleDriveAPI {
	private GoogleAuthorizationCodeFlow flow;
  private static final List<String> SCOPES = Arrays.asList(
		  "https://www.googleapis.com/auth/drive");

	public GoogleDriveAPI() {
		HttpTransport ht = new NetHttpTransport();
		JacksonFactory jf = new JacksonFactory();
		GoogleAuthorizationCodeFlow.Builder bder = 
				new GoogleAuthorizationCodeFlow.Builder(ht, jf, Constant.CLIENT_ID, Constant.CLIENT_SECRET, SCOPES);
		bder = bder.setAccessType("offline");
		flow = bder.build();
	}
		
	private Credential exchangeCode(String accCode) throws IOException {
		try {
			GoogleTokenResponse res = flow.newTokenRequest(accCode).execute();
			return flow.createAndStoreCredential(res, null);
		} catch (IOException e) {
			System.err.println("Error when Exchanging Code for Token");
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean getConfidentialByAccCode(String accCode) throws IOException {
		Credential credent;
		credent = exchangeCode(accCode);
		return true;
	}
}
