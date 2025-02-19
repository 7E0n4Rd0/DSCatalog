package com.leonardo.DSCatalog.projections;

public interface UserProjection {

    Long getId();
    String getFirstName();
    String getLastName();
    String getEmail();
    Long getRoleId();
    String getAuthority();
}
