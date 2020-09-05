package com.domain.ems.models.jwt;

public class Role {
	
	 private String authority;

	    public String getAuthority ()
	    {
	        return authority;
	    }

	    public void setAuthority (String authority)
	    {
	        this.authority = authority;
	    }

	    @Override
	    public String toString()
	    {
	        return "ClassPojo [authority = "+authority+"]";
	    }

}
