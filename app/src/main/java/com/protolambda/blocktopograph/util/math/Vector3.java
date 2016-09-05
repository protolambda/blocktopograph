package com.protolambda.blocktopograph.util.math;
import java.io.Serializable;

public class Vector3<T extends Number> implements Serializable {

    private static final long serialVersionUID = -3210132514862185790L;


    /**
     * the x-component of this vector
     **/
    public T x;
    /**
     * the y-component of this vector
     **/
    public T y;
    /**
     * the z-component of this vector
     **/
    public T z;

    /**
     * Creates a vector with the given components
     *
     * @param x The x-component
     * @param y The y-component
     * @param z The z-component
     */
    public Vector3(T x, T y, T z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}