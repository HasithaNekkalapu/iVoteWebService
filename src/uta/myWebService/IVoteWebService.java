package uta.myWebService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/testWS")
public class IVoteWebService {
	Connection conn = null;
	ResultSet rs = null;
    Statement stmt = null;
    java.sql.PreparedStatement prepStmt = null;
	/* DB Connection */
	public Connection dbConnection(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ivote", "root", "admin");
			return conn;
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;		
	}
	
	/* Called during Student and Admin login */
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/login")
	public String login(@QueryParam("emailId") String emailId, @QueryParam("pwd") String pwd){
		//System.out.println("Reached Here.. email= "+ emailId +" pwd="+pwd);
		try {
			conn = dbConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select fname,lname from users where emailID='"+emailId+"' and pwd='"+pwd+"' and isVerified = true");
			if(rs.next()){
				return "Successfull";
			}else{
				return "Unsuccessfull";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "Unsuccessfull";
	}
	
	/* Called during Student Registration */	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/register")
	public String register(@QueryParam("fname") String fname, @QueryParam("lname") String lname,
			@QueryParam("utaID") String utaID, @QueryParam("phone") String phone,
			@QueryParam("pwd") String pwd, @QueryParam("emailID") String emailID){
		String query = "INSERT INTO `ivote`.`students` "
				+ "( `fname`, `lname`, `utaID`, `phone`, `pwd`, `emailID`, `isAdmin`, `isVerified`) "
				+ "VALUES  ( ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try {
			conn = dbConnection();
			prepStmt = conn.prepareStatement(query);
			prepStmt.setString(1, fname);
			prepStmt.setString(2, lname);
			prepStmt.setFloat(3, Integer.parseInt(utaID));
			prepStmt.setString(4, phone);
			prepStmt.setString(5, pwd);
			prepStmt.setString(6, emailID);
			prepStmt.setString(7, "false");
			prepStmt.setString(8, "false");
			int isCreated = prepStmt.executeUpdate();
			if(isCreated > 0 ){
				return "Created";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "Not Registered";
		} finally {
			try {
				prepStmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "Registered";
	}
	
	/* Called for Register Verification */
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/registerVerification")
	public String registerVerification(@QueryParam("otp") String otp, @QueryParam("emailID") String emailID){
		
		try {
			conn = dbConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select fname from students where emailID='"+emailID+"' and otp='"+otp+"'");
			if(rs.next()){
				/* TODO: Update in DB for Student to is Verified*/
				return "Successfull";
			}else{
				return "Unsuccessfull";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "Unsuccessfull";
	}
	
	/* Called for Register Verification */
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/registerVerification")
	public String forgotPassword(@QueryParam("emailID") String emailID){
		
		try {
			conn = dbConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select emailID from students where emailID="+emailID);
			if(rs.next()){
				/* TODO: Send email to Registered Student */
				return "Email Sent";
			}else{
				return "Not Registered Student. Please Register Yourself";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "Not Registered Student. Please Register Yourself";
	}

	/* Poll Management - ADD */
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/createPoll")
	public String createPoll(@QueryParam("pollName") String pollName, @QueryParam("pollStartDate") String pollStartDate,
			@QueryParam("pollEndDate") String pollEndDate){
		String query = "INSERT INTO ivote.poll "
				+ "( pollName, pollStartDate, pollEndDate, isActive, isResultNotified) "
				+ "VALUES (?, ?, ?, ?, ?)";
		
		try {
			conn = dbConnection();
			prepStmt = conn.prepareStatement(query);
			prepStmt.setString(1, pollName);
			prepStmt.setString(2, pollStartDate);
			prepStmt.setString(3, pollEndDate);
			prepStmt.setString(4, "false");
			prepStmt.setString(5, "false");
			int isCreated = prepStmt.executeUpdate();
			if(isCreated > 0 ){
				return "Created";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "Not Created";
		} finally {
			try {
				prepStmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "Not Created";
	}

}
