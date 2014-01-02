package tube;

import java.sql.Timestamp;

public class VideoVO {

	public VideoVO() {}

	public VideoVO(String name, Timestamp ts, String s3link, String cflink, int rating, int totalvotes, String date)
	{
		this.name = name;
		this.ts = ts;
		this.s3link = s3link;
		this.cflink = cflink;
		this.rating = rating;
		this.totalvotes = totalvotes;
		this.date = date;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	private String name;
	private Timestamp ts;
	private String s3link;
	private String cflink;
	private int rating;
	private int totalvotes;
	private String date;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Timestamp getTs() {
		return ts;
	}
	public void setTs(Timestamp ts) {
		this.ts = ts;
	}
	public String getS3link() {
		return s3link;
	}
	public void setS3link(String s3link) {
		this.s3link = s3link;
	}
	public String getCflink() {
		return cflink;
	}
	public void setCflink(String cflink) {
		this.cflink = cflink;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public int getTotalvotes() {
		return totalvotes;
	}
	public void setTotalvotes(int totalvotes) {
		this.totalvotes = totalvotes;
	}

	public String toString()
	{
		return "name: " + name + ", date: " + date + ", s3link: " + s3link + 
				", cflink: " + cflink + ", rating: " + rating + ", totalvotes: " + totalvotes;
	}
}
