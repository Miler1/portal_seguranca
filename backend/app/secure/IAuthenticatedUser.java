package secure;

public interface IAuthenticatedUser {

	Integer getId();

	String getName();

	String[] getRoles();

	boolean hasRole(String role);

}
