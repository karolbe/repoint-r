package com.documentum.devprog.common;

public abstract interface IAssertion
{
  public static final int LEVEL_ERROR = 1;
  public static final int LEVEL_EXCEPTION = 2;
  public static final int LEVEL_LOG = 64;

  public abstract void jdMethod_assert(boolean paramBoolean, String paramString);
}

/* Location:           devprogClasses.jar
 * Qualified Name:     com.documentum.devprog.common.IAssertion
 * JD-Core Version:    0.6.2
 */