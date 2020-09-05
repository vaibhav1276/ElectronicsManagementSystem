package com.domain.ems.models.jwt;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Token {
	 @JsonProperty("Role")
	private List<Role> Role;

    private String sub;

    private String exp;

    private String iat;

    private String jti;

    private String username;



    public List<Role> getRole() {
		return Role;
	}

	public void setRole(List<Role> role) {
		Role = role;
	}

	public String getSub ()
    {
        return sub;
    }

    public void setSub (String sub)
    {
        this.sub = sub;
    }

    public String getExp ()
    {
        return exp;
    }

    public void setExp (String exp)
    {
        this.exp = exp;
    }

    public String getIat ()
    {
        return iat;
    }

    public void setIat (String iat)
    {
        this.iat = iat;
    }

    public String getJti ()
    {
        return jti;
    }

    public void setJti (String jti)
    {
        this.jti = jti;
    }

    public String getUsername ()
    {
        return username;
    }

    public void setUsername (String username)
    {
        this.username = username;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [Role = "+Role+", sub = "+sub+", exp = "+exp+", iat = "+iat+", jti = "+jti+", username = "+username+"]";
    }

}
