package com.higgses.griffin.exception;

public class GinDBNotOpenException extends Exception
{
	private static final long serialVersionUID = 1L;

	public GinDBNotOpenException()
	{
		super();
	}

	public GinDBNotOpenException(String detailMessage)
	{
		super(detailMessage);
	}

}
