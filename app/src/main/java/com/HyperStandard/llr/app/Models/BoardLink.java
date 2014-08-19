package com.HyperStandard.llr.app.Models;

/**
 * Created by nonex_000 on 8/18/2014.
 */
public class BoardLink
{
	private String boardName;
	private String boardURL;

	public BoardLink( String boardName, String boardURL )
	{
		this.boardName = boardName;
		this.boardURL = boardURL;
	}

	public String getBoardName()
	{
		return boardName;
	}

	public String getBoardURL()
	{
		return boardURL;
	}
}
