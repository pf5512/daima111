/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;

public class btTransformFloatData extends BulletBase {
	private long swigCPtr;
	
	protected btTransformFloatData(final String className, long cPtr, boolean cMemoryOwn) {
		super(className, cPtr, cMemoryOwn);
		swigCPtr = cPtr;
	}
	
	/** Construct a new btTransformFloatData, normally you should not need this constructor it's intended for low-level usage. */ 
	public btTransformFloatData(long cPtr, boolean cMemoryOwn) {
		this("btTransformFloatData", cPtr, cMemoryOwn);
		construct();
	}
	
	@Override
	protected void reset(long cPtr, boolean cMemoryOwn) {
		if (!destroyed)
			destroy();
		super.reset(swigCPtr = cPtr, cMemoryOwn);
	}
	
	public static long getCPtr(btTransformFloatData obj) {
		return (obj == null) ? 0 : obj.swigCPtr;
	}

	@Override
	protected void finalize() throws Throwable {
		if (!destroyed)
			destroy();
		super.finalize();
	}

  @Override protected synchronized void delete() {
		if (swigCPtr != 0) {
			if (swigCMemOwn) {
				swigCMemOwn = false;
				LinearMathJNI.delete_btTransformFloatData(swigCPtr);
			}
			swigCPtr = 0;
		}
		super.delete();
	}

  public void setBasis(SWIGTYPE_p_btMatrix3x3FloatData value) {
    LinearMathJNI.btTransformFloatData_basis_set(swigCPtr, this, SWIGTYPE_p_btMatrix3x3FloatData.getCPtr(value));
  }

  public SWIGTYPE_p_btMatrix3x3FloatData getBasis() {
    return new SWIGTYPE_p_btMatrix3x3FloatData(LinearMathJNI.btTransformFloatData_basis_get(swigCPtr, this), true);
  }

  public void setOrigin(btVector3FloatData value) {
    LinearMathJNI.btTransformFloatData_origin_set(swigCPtr, this, btVector3FloatData.getCPtr(value), value);
  }

  public btVector3FloatData getOrigin() {
    long cPtr = LinearMathJNI.btTransformFloatData_origin_get(swigCPtr, this);
    return (cPtr == 0) ? null : new btVector3FloatData(cPtr, false);
  }

  public btTransformFloatData() {
    this(LinearMathJNI.new_btTransformFloatData(), true);
  }

}