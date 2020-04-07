/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.internal.lifecycle.phases;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import java.io.Closeable;
import java.io.Serializable;

import org.junit.Test;

public class DefaultLifecycleObjectSorterTestCase {

  @Test
  public void objectsAreSorted()  {
    DefaultLifecycleObjectSorter sorter = new DefaultLifecycleObjectSorter(new Class[] {Integer.class, String.class, Float.class});
    final Float float1 = 1.0f;
    final Float float2 = 2.0f;
    final Integer int1 = 1;
    final Integer int2 = 2;
    final String string1 = "string1";
    final String string2 = "string2";

    sorter.addObject("float1", float1);
    sorter.addObject("float2", float2);
    sorter.addObject("int1", int1);
    sorter.addObject("int2", int2);
    sorter.addObject("string1", string1);
    sorter.addObject("string2", string2);

    assertThat(sorter.getSortedObjects(), contains(int1, int2, string1, string2, float1, float2));
  }

  @Test
  public void objectsAreNotRepeatedIfAddedMultipleTimes() {
    DefaultLifecycleObjectSorter sorter = new DefaultLifecycleObjectSorter(new Class[] {Integer.class});
    final Integer myInt = 1;
    sorter.addObject("int1", myInt);
    sorter.addObject("int2", myInt);
    sorter.addObject("int3", myInt);
    assertThat(sorter.getSortedObjects(), hasSize(1));
  }

  @Test
  public void objectsAreNotRepeatedIfTheyImplementMultipleBucketClasses() {
    DefaultLifecycleObjectSorter sorter = new DefaultLifecycleObjectSorter(new Class[] {Serializable.class, Closeable.class});
    Serializable myObject = mock(Serializable.class, withSettings().extraInterfaces(Closeable.class));

    sorter.addObject("myObject", myObject);
    assertThat(sorter.getSortedObjects(), hasSize(1));
  }


}
