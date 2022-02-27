package models;

import java.util.List;

public class Pagination<T> {

	private Long totalItems;
	private List<T> pageItems;

	public Pagination(Long totalItems, List<T> pageItems) {

		super();

		this.totalItems = totalItems;
		this.pageItems = pageItems;

	}

	public Long getTotalItems() {

		return totalItems;

	}

	public void setTotalResults(Long totalResults) {

		this.totalItems = totalResults;

	}

	public List<T> getPageItems() {

		return pageItems;

	}

	public void setPageItems(List<T> pageItems) {

		this.pageItems = pageItems;

	}

}