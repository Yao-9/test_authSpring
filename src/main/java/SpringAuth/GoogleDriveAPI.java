package SpringAuth;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfoplus;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.io.InputStreamReader;

public class GoogleDriveAPI {
	private static final String CLIENTSECRETS_LOCATION = "client_secrets.json";

	private static final String REDIRECT_URI = "http://localhost";
	private static final List<String> SCOPES = Arrays.asList(
			"https://www.googleapis.com/auth/drive.file",
			"email",
			"profile");

	private static GoogleAuthorizationCodeFlow flow = null;

	public static class GetCredentialsException extends Exception {

		protected String authorizationUrl;

		public GetCredentialsException(String authorizationUrl) {
			this.authorizationUrl = authorizationUrl;
		}

		public void setAuthorizationUrl(String authorizationUrl) {
			this.authorizationUrl = authorizationUrl;
		}

		public String getAuthorizationUrl() {
			return authorizationUrl;
		}
	}

	public static class CodeExchangeException extends GetCredentialsException {

		public CodeExchangeException(String authorizationUrl) {
			super(authorizationUrl);
		}
	}

	public static class NoRefreshTokenException extends GetCredentialsException {

		public NoRefreshTokenException(String authorizationUrl) {
			super(authorizationUrl);
		}
	}

	private static class NoUserIdException extends Exception {
	}

	static Credential getStoredCredentials(String userId) {
		return Storage.data.get(userId);
	}

	static void storeCredentials(String userId, Credential credentials) {
		Storage.data.put(userId, credentials);
	}

	static GoogleAuthorizationCodeFlow getFlow() throws IOException {
		if (flow == null) {
			HttpTransport httpTransport = new NetHttpTransport();
			JacksonFactory jsonFactory = new JacksonFactory();
			GoogleClientSecrets clientSecrets =	GoogleClientSecrets.load(jsonFactory,
					new InputStreamReader(GoogleDriveAPI.class.getResourceAsStream(CLIENTSECRETS_LOCATION)));
			flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory,
					clientSecrets, SCOPES).setAccessType("offline").
					setApprovalPrompt("force").build();
		}

		return flow;
	}

	static Credential exchangeCode(String authorizationCode)
		throws CodeExchangeException {
		try {
			GoogleAuthorizationCodeFlow flow = getFlow();
			GoogleTokenResponse response =  
				flow.newTokenRequest(authorizationCode).setRedirectUri(REDIRECT_URI).execute();
			return flow.createAndStoreCredential(response, null);
		} catch (IOException e) {
			System.err.println("An error occurred: " + e);
			throw new CodeExchangeException(null);
		}
	}

	/**
	 * Send a request to the UserInfo API to retrieve the user's information.
	 *
	 * @param credentials OAuth 2.0 credentials to authorize the request.
	 * @return User's information.
	 * @throws NoUserIdException An error occurred.
	 */
	static Userinfoplus getUserInfo(Credential credentials)
		throws NoUserIdException {
		Oauth2 userInfoService =
			new Oauth2.Builder(new NetHttpTransport(), new JacksonFactory(), credentials).build();
		Userinfoplus userInfo = null;
		try {
			userInfo = userInfoService.userinfo().get().execute();
		} catch (IOException e) {
			System.err.println("An error occurred: " + e);
		}
		if (userInfo != null && userInfo.getId() != null) {
			return userInfo;
		} else {
			throw new NoUserIdException();
		}
	}

	/**
	 * Retrieve the authorization URL.
	 *
	 * @param emailAddress User's e-mail address.
	 * @param state State for the authorization URL.
	 * @return Authorization URL to redirect the user to.
	 * @throws IOException Unable to load client_secrets.json.
	 */
	public static String getAuthorizationUrl(String emailAddress, String state) throws IOException {
		GoogleAuthorizationCodeRequestUrl urlBuilder =
			getFlow().newAuthorizationUrl().setRedirectUri(REDIRECT_URI).setState(state);
		urlBuilder.set("user_id", emailAddress);
		return urlBuilder.build();
	}

	/**
	 * Retrieve credentials using the provided authorization code.
	 *
	 * This function exchanges the authorization code for an access token and
	 * queries the UserInfo API to retrieve the user's e-mail address. If a
	 * refresh token has been retrieved along with an access token, it is stored
	 * in the application database using the user's e-mail address as key. If no
	 * refresh token has been retrieved, the function checks in the application
	 * database for one and returns it if found or throws a NoRefreshTokenException
	 * with the authorization URL to redirect the user to.
	 *
	 * @param authorizationCode Authorization code to use to retrieve an access
	 *        token.
	 * @param state State to set to the authorization URL in case of error.
	 * @return OAuth 2.0 credentials instance containing an access and refresh
	 *         token.
	 * @throws NoRefreshTokenException No refresh token could be retrieved from
	 *         the available sources.
	 * @throws IOException Unable to load client_secrets.json.
	 */
	public static Credential getCredentials(String authorizationCode, String state)
		throws CodeExchangeException, NoRefreshTokenException, IOException {
		String emailAddress = "";
		try {
			Credential credentials = exchangeCode(authorizationCode);
			Userinfoplus userInfo = getUserInfo(credentials);
			String userId = userInfo.getId();
			emailAddress = userInfo.getEmail();
			if (credentials.getRefreshToken() != null) {
				storeCredentials(userId, credentials);
				return credentials;
			} else {
				credentials = getStoredCredentials(userId);
				if (credentials != null && credentials.getRefreshToken() != null) {
					return credentials;
				}
			}
		} catch (CodeExchangeException e) {
			e.printStackTrace();
			e.setAuthorizationUrl(getAuthorizationUrl(emailAddress, state));
			throw e;
		} catch (NoUserIdException e) {
			e.printStackTrace();
		}
		// No refresh token has been retrieved.
		String authorizationUrl = getAuthorizationUrl(emailAddress, state);
		throw new NoRefreshTokenException(authorizationUrl);
	}
}
