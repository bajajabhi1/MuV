import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MySqlAPI {
	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	private String selectAll = "select * from VIDEO_INFO";
	private String DB_END_POINT = "ab3900MySql";
	private final String DB_USER_NAME = "ab3900_user";
	private final String DB_PWD = "ab3900_pass";
	private final String DB_NAME = "ab3900db";
	private final int DB_PORT = 3306;
	
	public MySqlAPI(String rdsEP)
	{
		this.DB_END_POINT = rdsEP;
	}
	
	public static void main(String args[])
	{
		MySqlAPI mysql = new MySqlAPI("");
		try {
			//mysql.dropTable();
			//mysql.createTable();
			/*mysql.readAll();
			VideoVO vo = new VideoVO("Zara", new Timestamp(System.currentTimeMillis()), "s3link", "cflink", 5, 7);
			mysql.insertEntry(vo);
			vo = new VideoVO("Alice", new Timestamp(System.currentTimeMillis()), "s3link", "cflink", 5, 7);
			mysql.insertEntry(vo);
			mysql.readAll();
			mysql.updateRating("Zara", 55);*/
			//mysql.deleteEntry("Zara");
			//mysql.readAll();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteEntry(String name)
	{
		try {
			createConnectionAndStatement();
			String deleteSql = "DELETE FROM VIDEO_INFO where name='" + name + "'";
			statement.executeUpdate(deleteSql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	
	public VideoVO getEntry(String name)
	{
		try {
			createConnectionAndStatement();
			String insertSql = "select * from VIDEO_INFO where name='" + name +"'";
			resultSet  = statement.executeQuery(insertSql);
			List<VideoVO> list = parseResultSet(resultSet);
			return list.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return null;
	}

	public void insertEntry(VideoVO vo)
	{
		try {
			createConnectionAndStatement();
			String insertSql = "INSERT INTO VIDEO_INFO VALUES ('" + vo.getName() +"','"+ vo.getTs()+
					"','"+vo.getS3link()+"','"+vo.getCflink()+"',"+vo.getRating()+ ", "+vo.getTotalvotes()+" )";
			statement.executeUpdate(insertSql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	
	public void addVote(String name)
	{
		try {
			createConnectionAndStatement();
			VideoVO vo = getEntry(name);
			int votes = vo.getTotalvotes();
			votes = votes+1;
			updateTotalVotes(name, votes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	public void updateRating(String name, int rating)
	{
		try {
			createConnectionAndStatement();
			String updateSql = "UPDATE VIDEO_INFO SET rating=" + rating +"  where name='" + name + "'";
			statement.executeUpdate(updateSql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	
	public void updateTotalVotes(String name, int votes)
	{
		try {
			createConnectionAndStatement();
			String updateSql = "UPDATE VIDEO_INFO SET totalvotes=" + votes +"  where name='" + name + "'";
			statement.executeUpdate(updateSql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	public List<VideoVO> readAll()
	{
		List<VideoVO> list = new ArrayList<VideoVO>();
		try {
			createConnectionAndStatement();
			resultSet = statement.executeQuery(selectAll);
			list =  parseResultSet(resultSet);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return list;
	}

	public void createConnectionAndStatement()
	{
		try{
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:mysql://"+DB_END_POINT+":"+DB_PORT+"/"+DB_NAME,DB_USER_NAME,DB_PWD);

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
			close();
		}
	}

	public void createTable()
	{
		try {
			createConnectionAndStatement();
			String createTableSql = "CREATE TABLE VIDEO_INFO (name VARCHAR(255) not NULL, timestamp TIMESTAMP, " + 
					" s3link VARCHAR(255), cflink VARCHAR(255), rating INTEGER, totalvotes INTEGER)";
			statement.executeUpdate(createTableSql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}

	}
	
	public void dropTable()
	{
		try {
			createConnectionAndStatement();
			statement.executeUpdate("drop table VIDEO_INFO");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}

	}

	private List<VideoVO> parseResultSet(ResultSet resultSet) throws SQLException {
		// ResultSet is initially before the first data set
		List<VideoVO> list = new ArrayList<VideoVO>();
		while (resultSet.next()) {
			
			VideoVO vo = new VideoVO();
			vo.setName(resultSet.getString("name"));
			vo.setTs(resultSet.getTimestamp("timestamp"));
			vo.setS3link(resultSet.getString("s3link"));
			vo.setCflink(resultSet.getString("cflink"));
			vo.setRating(resultSet.getInt("rating"));
			vo.setTotalvotes(resultSet.getInt("totalvotes"));
			System.out.println(vo.toString());
			list.add(vo);
		}
		return list;
	}

	// You need to close the resultSet
	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}

} 