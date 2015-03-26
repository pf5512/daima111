/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.badlogic.gdx.physics.bullet.softbody;

import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.linearmath.*;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;

public class btSparseSdf3 extends BulletBase {
	private long swigCPtr;
	
	protected btSparseSdf3(final String className, long cPtr, boolean cMemoryOwn) {
		super(className, cPtr, cMemoryOwn);
		swigCPtr = cPtr;
	}
	
	/** Construct a new btSparseSdf3, normally you should not need this constructor it's intended for low-level usage. */ 
	public btSparseSdf3(long cPtr, boolean cMemoryOwn) {
		this("btSparseSdf3", cPtr, cMemoryOwn);
		construct();
	}
	
	@Override
	protected void reset(long cPtr, boolean cMemoryOwn) {
		if (!destroyed)
			destroy();
		super.reset(swigCPtr = cPtr, cMemoryOwn);
	}
	
	public static long getCPtr(btSparseSdf3 obj) {
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
				SoftbodyJNI.delete_btSparseSdf3(swigCPtr);
			}
			swigCPtr = 0;
		}
		super.delete();
	}

  static public class IntFrac extends BulletBase {
  	private long swigCPtr;
  	
  	protected IntFrac(final String className, long cPtr, boolean cMemoryOwn) {
  		super(className, cPtr, cMemoryOwn);
  		swigCPtr = cPtr;
  	}
  	
  	/** Construct a new IntFrac, normally you should not need this constructor it's intended for low-level usage. */ 
  	public IntFrac(long cPtr, boolean cMemoryOwn) {
  		this("IntFrac", cPtr, cMemoryOwn);
  		construct();
  	}
  	
  	@Override
  	protected void reset(long cPtr, boolean cMemoryOwn) {
  		if (!destroyed)
  			destroy();
  		super.reset(swigCPtr = cPtr, cMemoryOwn);
  	}
  	
  	public static long getCPtr(IntFrac obj) {
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
  				SoftbodyJNI.delete_btSparseSdf3_IntFrac(swigCPtr);
  			}
  			swigCPtr = 0;
  		}
  		super.delete();
  	}
  
    public void setB(int value) {
      SoftbodyJNI.btSparseSdf3_IntFrac_b_set(swigCPtr, this, value);
    }
  
    public int getB() {
      return SoftbodyJNI.btSparseSdf3_IntFrac_b_get(swigCPtr, this);
    }
  
    public void setI(int value) {
      SoftbodyJNI.btSparseSdf3_IntFrac_i_set(swigCPtr, this, value);
    }
  
    public int getI() {
      return SoftbodyJNI.btSparseSdf3_IntFrac_i_get(swigCPtr, this);
    }
  
    public void setF(float value) {
      SoftbodyJNI.btSparseSdf3_IntFrac_f_set(swigCPtr, this, value);
    }
  
    public float getF() {
      return SoftbodyJNI.btSparseSdf3_IntFrac_f_get(swigCPtr, this);
    }
  
    public IntFrac() {
      this(SoftbodyJNI.new_btSparseSdf3_IntFrac(), true);
    }
  
  }

  static public class Cell extends BulletBase {
  	private long swigCPtr;
  	
  	protected Cell(final String className, long cPtr, boolean cMemoryOwn) {
  		super(className, cPtr, cMemoryOwn);
  		swigCPtr = cPtr;
  	}
  	
  	/** Construct a new Cell, normally you should not need this constructor it's intended for low-level usage. */ 
  	public Cell(long cPtr, boolean cMemoryOwn) {
  		this("Cell", cPtr, cMemoryOwn);
  		construct();
  	}
  	
  	@Override
  	protected void reset(long cPtr, boolean cMemoryOwn) {
  		if (!destroyed)
  			destroy();
  		super.reset(swigCPtr = cPtr, cMemoryOwn);
  	}
  	
  	public static long getCPtr(Cell obj) {
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
  				SoftbodyJNI.delete_btSparseSdf3_Cell(swigCPtr);
  			}
  			swigCPtr = 0;
  		}
  		super.delete();
  	}
  
    public void setD(SWIGTYPE_p_a_3_1__a_3_1__float value) {
      SoftbodyJNI.btSparseSdf3_Cell_d_set(swigCPtr, this, SWIGTYPE_p_a_3_1__a_3_1__float.getCPtr(value));
    }
  
    public SWIGTYPE_p_a_3_1__a_3_1__float getD() {
      long cPtr = SoftbodyJNI.btSparseSdf3_Cell_d_get(swigCPtr, this);
      return (cPtr == 0) ? null : new SWIGTYPE_p_a_3_1__a_3_1__float(cPtr, false);
    }
  
    public void setC(int[] value) {
      SoftbodyJNI.btSparseSdf3_Cell_c_set(swigCPtr, this, value);
    }
  
    public int[] getC() {
      return SoftbodyJNI.btSparseSdf3_Cell_c_get(swigCPtr, this);
  }
  
    public void setPuid(int value) {
      SoftbodyJNI.btSparseSdf3_Cell_puid_set(swigCPtr, this, value);
    }
  
    public int getPuid() {
      return SoftbodyJNI.btSparseSdf3_Cell_puid_get(swigCPtr, this);
    }
  
    public void setHash(long value) {
      SoftbodyJNI.btSparseSdf3_Cell_hash_set(swigCPtr, this, value);
    }
  
    public long getHash() {
      return SoftbodyJNI.btSparseSdf3_Cell_hash_get(swigCPtr, this);
    }
  
    public void setPclient(btCollisionShape value) {
      SoftbodyJNI.btSparseSdf3_Cell_pclient_set(swigCPtr, this, btCollisionShape.getCPtr(value), value);
    }
  
    public btCollisionShape getPclient() {
      long cPtr = SoftbodyJNI.btSparseSdf3_Cell_pclient_get(swigCPtr, this);
      return (cPtr == 0) ? null : btCollisionShape.newDerivedObject(cPtr, false);
    }
  
    public void setNext(btSparseSdf3.Cell value) {
      SoftbodyJNI.btSparseSdf3_Cell_next_set(swigCPtr, this, btSparseSdf3.Cell.getCPtr(value), value);
    }
  
    public btSparseSdf3.Cell getNext() {
      long cPtr = SoftbodyJNI.btSparseSdf3_Cell_next_get(swigCPtr, this);
      return (cPtr == 0) ? null : new btSparseSdf3.Cell(cPtr, false);
    }
  
    public Cell() {
      this(SoftbodyJNI.new_btSparseSdf3_Cell(), true);
    }
  
  }

  public void setCells(SWIGTYPE_p_btAlignedObjectArrayT_btSparseSdfT_3_t__Cell_p_t value) {
    SoftbodyJNI.btSparseSdf3_cells_set(swigCPtr, this, SWIGTYPE_p_btAlignedObjectArrayT_btSparseSdfT_3_t__Cell_p_t.getCPtr(value));
  }

  public SWIGTYPE_p_btAlignedObjectArrayT_btSparseSdfT_3_t__Cell_p_t getCells() {
    long cPtr = SoftbodyJNI.btSparseSdf3_cells_get(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_btAlignedObjectArrayT_btSparseSdfT_3_t__Cell_p_t(cPtr, false);
  }

  public void setVoxelsz(float value) {
    SoftbodyJNI.btSparseSdf3_voxelsz_set(swigCPtr, this, value);
  }

  public float getVoxelsz() {
    return SoftbodyJNI.btSparseSdf3_voxelsz_get(swigCPtr, this);
  }

  public void setPuid(int value) {
    SoftbodyJNI.btSparseSdf3_puid_set(swigCPtr, this, value);
  }

  public int getPuid() {
    return SoftbodyJNI.btSparseSdf3_puid_get(swigCPtr, this);
  }

  public void setNcells(int value) {
    SoftbodyJNI.btSparseSdf3_ncells_set(swigCPtr, this, value);
  }

  public int getNcells() {
    return SoftbodyJNI.btSparseSdf3_ncells_get(swigCPtr, this);
  }

  public void setClampCells(int value) {
    SoftbodyJNI.btSparseSdf3_clampCells_set(swigCPtr, this, value);
  }

  public int getClampCells() {
    return SoftbodyJNI.btSparseSdf3_clampCells_get(swigCPtr, this);
  }

  public void setNprobes(int value) {
    SoftbodyJNI.btSparseSdf3_nprobes_set(swigCPtr, this, value);
  }

  public int getNprobes() {
    return SoftbodyJNI.btSparseSdf3_nprobes_get(swigCPtr, this);
  }

  public void setNqueries(int value) {
    SoftbodyJNI.btSparseSdf3_nqueries_set(swigCPtr, this, value);
  }

  public int getNqueries() {
    return SoftbodyJNI.btSparseSdf3_nqueries_get(swigCPtr, this);
  }

  public void Initialize(int hashsize, int clampCells) {
    SoftbodyJNI.btSparseSdf3_Initialize__SWIG_0(swigCPtr, this, hashsize, clampCells);
  }

  public void Initialize(int hashsize) {
    SoftbodyJNI.btSparseSdf3_Initialize__SWIG_1(swigCPtr, this, hashsize);
  }

  public void Initialize() {
    SoftbodyJNI.btSparseSdf3_Initialize__SWIG_2(swigCPtr, this);
  }

  public void Reset() {
    SoftbodyJNI.btSparseSdf3_Reset(swigCPtr, this);
  }

  public void GarbageCollect(int lifetime) {
    SoftbodyJNI.btSparseSdf3_GarbageCollect__SWIG_0(swigCPtr, this, lifetime);
  }

  public void GarbageCollect() {
    SoftbodyJNI.btSparseSdf3_GarbageCollect__SWIG_1(swigCPtr, this);
  }

  public int RemoveReferences(btCollisionShape pcs) {
    return SoftbodyJNI.btSparseSdf3_RemoveReferences(swigCPtr, this, btCollisionShape.getCPtr(pcs), pcs);
  }

  public float Evaluate(Vector3 x, btCollisionShape shape, Vector3 normal, float margin) {
    return SoftbodyJNI.btSparseSdf3_Evaluate(swigCPtr, this, x, btCollisionShape.getCPtr(shape), shape, normal, margin);
  }

  public void BuildCell(btSparseSdf3.Cell c) {
    SoftbodyJNI.btSparseSdf3_BuildCell(swigCPtr, this, btSparseSdf3.Cell.getCPtr(c), c);
  }

  public static float DistanceToShape(Vector3 x, btCollisionShape shape) {
    return SoftbodyJNI.btSparseSdf3_DistanceToShape(x, btCollisionShape.getCPtr(shape), shape);
  }

  public static btSparseSdf3.IntFrac Decompose(float x) {
    return new btSparseSdf3.IntFrac(SoftbodyJNI.btSparseSdf3_Decompose(x), true);
  }

  public static float Lerp(float a, float b, float t) {
    return SoftbodyJNI.btSparseSdf3_Lerp(a, b, t);
  }

  public static long Hash(int x, int y, int z, btCollisionShape shape) {
    return SoftbodyJNI.btSparseSdf3_Hash(x, y, z, btCollisionShape.getCPtr(shape), shape);
  }

  public btSparseSdf3() {
    this(SoftbodyJNI.new_btSparseSdf3(), true);
  }

}