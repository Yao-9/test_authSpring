package packet;

public class AccessCodePacket {

	private String AccessCode;
	private String name;
	private String userID;
	
	public AccessCodePacket(String AccessCode, String name, String userID) {
		this.AccessCode = AccessCode;
		this.name = name;
		this.userID = userID;
	}
	
	public String getAccessCode( ) { return this.AccessCode; }

	public String getName( ) { return this.name; }

	public String getUserID( ) { return this.userID; }

}
