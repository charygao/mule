/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.internal.lifecycle.phases;

import static java.lang.System.identityHashCode;
import static java.util.stream.Collectors.toList;
import org.mule.runtime.core.internal.registry.Registry;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link LifecycleObjectSorter}.
 * <p>
 * It works by classifying objects into buckets depending their type, to finally
 * merge all the buckets together
 *
 * @since 4.2
 */
public class DefaultLifecycleObjectSorter implements LifecycleObjectSorter {

  private List<Object>[] buckets;
  private int objectCount = 0;
  protected Class<?>[] orderedLifecycleTypes;

  /**
   * Creates a new instance
   *
   * @param orderedLifecycleTypes an ordered array specifying a type based order
   */
  public DefaultLifecycleObjectSorter(Class<?>[] orderedLifecycleTypes) {
    this.orderedLifecycleTypes = orderedLifecycleTypes;
    buckets = new List[orderedLifecycleTypes.length];
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addObject(String name, Object object) {
    for (int i = 0; i < orderedLifecycleTypes.length; i++) {
      if (orderedLifecycleTypes[i].isInstance(object)) {
        List<Object> bucket = buckets[i];
        if (bucket == null) {
          bucket = new LinkedList<>();
          buckets[i] = bucket;
        }
        objectCount += doAddObject(name, object, bucket);
        break;
      }
    }
  }

  /**
   * Actually adds the given {@code object} to the given {@code bucket}.
   * <p>
   * Implementors are free to add additional objects to the bucket, in any particular position. This default
   * implementation however only adds the given one at the end of the list
   *
   * @param name   the name under which the object is registered in the {@link Registry}
   * @param object the object
   * @param bucket the bucket in which the object(s) are to be added
   * @return how many objects were added
   */
  protected int doAddObject(String name, Object object, List<Object> bucket) {
    bucket.add(object);
    return 1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Object> getSortedObjects() {
    Set<IdentityWrapper> sorted = new LinkedHashSet<>(objectCount);
    for (List<Object> bucket : buckets) {
      if (bucket != null) {
        bucket.forEach(e -> sorted.add(new IdentityWrapper(e)));
      }
    }
    return sorted.stream().map(w -> w.delegate).collect(toList());
  }

  private static class IdentityWrapper {

    private Object delegate;

    private IdentityWrapper(Object delegate) {
      this.delegate = delegate;
    }

    @Override
    public int hashCode() {
      return identityHashCode(delegate);
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof IdentityWrapper)) {
        return false;
      }
      IdentityWrapper otherWrapped = (IdentityWrapper)obj;
      return this.delegate == otherWrapped.delegate;
    }
  }

}
