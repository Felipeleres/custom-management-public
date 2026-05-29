package com.felipeleres.customermanagement.projections;

public interface UserDetailsProjection {

     String getUsername();
     String getPassword();
     Long getRoleId();
     String getAuthority();

}
