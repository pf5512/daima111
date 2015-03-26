/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.linearmath.*;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;

public class btUniversalConstraint extends btGeneric6DofConstraint {
	private long swigCPtr;
	
	protected btUniversalConstraint(final String className, long cPtr, boolean cMemoryOwn) {
		super(className, DynamicsJNI.btUniversalConstraint_SWIGUpcast(cPtr), cMemoryOwn);
		swigCPtr = cPtr;
	}
	
	/** Construct a new btUniversalConstraint, normally you should not need this constructor it's intended for low-level usage. */
	public btUniversalConstraint(long cPtr, boolean cMemoryOwn) {
		this("btUniversalConstraint", cPtr, cMemoryOwn);
		construct();
	}
	
	@Override
	protected void reset(long cPtr, boolean cMemoryOwn) {
		if (!destroyed)
			destroy();
		super.reset(DynamicsJNI.btUniversalConstraint_SWIGUpcast(swigCPtr = cPtr), cMemoryOwn);
	}
	
	public static long getCPtr(btUniversalConstraint obj) {
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
				DynamicsJNI.delete_btUniversalConstraint(swigCPtr);
			}
			swigCPtr = 0;
		}
		super.delete();
	}

  public btUniversalConstraint(btRigidBody rbA, btRigidBody rbB, Vector3 anchor, Vector3 axis1, Vector3 axis2) {
    this(DynamicsJNI.new_btUniversalConstraint(btRigidBody.getCPtr(rbA), rbA, btRigidBody.getCPtr(rbB), rbB, anchor, axis1, axis2), true);
  }

  public Vector3 getAnchor() {
	return DynamicsJNI.btUniversalConstraint_getAnchor(swigCPtr, this);
}

  public Vector3 getAnchor2() {
	return DynamicsJNI.btUniversalConstraint_getAnchor2(swigCPtr, this);
}

  public Vector3 getAxis1() {
	return DynamicsJNI.btUniversalConstraint_getAxis1(swigCPtr, this);
}

  public Vector3 getAxis2() {
	return DynamicsJNI.btUniversalConstraint_getAxis2(swigCPtr, this);
}

  public float getAngle1() {
    return DynamicsJNI.btUniversalConstraint_getAngle1(swigCPtr, this);
  }

  public float getAngle2() {
    return DynamicsJNI.btUniversalConstraint_getAngle2(swigCPtr, this);
  }

  public void setUpperLimit(float ang1max, float ang2max) {
    DynamicsJNI.btUniversalConstraint_setUpperLimit(swigCPtr, this, ang1max, ang2max);
  }

  public void setLowerLimit(float ang1min, float ang2min) {
    DynamicsJNI.btUniversalConstraint_setLowerLimit(swigCPtr, this, ang1min, ang2min);
  }

  public void setAxis(Vector3 axis1, Vector3 axis2) {
    DynamicsJNI.btUniversalConstraint_setAxis(swigCPtr, this, axis1, axis2);
  }

}