package com.leonardo.DSCatalog.projections;

/**
 * Generic projection to implements in any class that uses id
 * as a primary key in relational model.
 * @param <E> id of the class implemented <br>
 * <pre>{@code
 * public class Product implements IdProjection<Long>{
 *     @Id
 *     @GeneratedValue(strategy = GenerationType.IDENTITY)
 *     private Long id;
 *     ***
 *}
 *}
 *</pre>
 */
public interface IdProjection<E> {
    E getId();
}
