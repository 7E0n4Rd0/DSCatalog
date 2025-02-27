package com.leonardo.DSCatalog.projections;

public interface UserProjection extends IdProjection<Long>{
    String getFirstName();
    String getLastName();
    String getEmail();

}
