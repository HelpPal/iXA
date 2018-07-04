package com.ixplore.com;


/**
 * @author javatechig {@link http://javatechig.com}
 * 
 */
public class CategoryImageItem {
	private String imageUrl;
	private String title;

	public CategoryImageItem(String imageUrl, String title) {
		super();
		this.imageUrl = imageUrl;
		this.title = title;		
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
		
}
