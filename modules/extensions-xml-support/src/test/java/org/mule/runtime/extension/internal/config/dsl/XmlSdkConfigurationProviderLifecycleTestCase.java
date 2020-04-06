/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.extension.internal.config.dsl;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.disposeIfNeeded;
import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.initialiseIfNeeded;
import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.startIfNeeded;
import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.stopIfNeeded;
import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.Lifecycle;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.api.meta.model.ExtensionModel;
import org.mule.runtime.api.meta.model.config.ConfigurationModel;
import org.mule.runtime.core.api.Injector;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.config.MuleConfiguration;
import org.mule.runtime.extension.api.runtime.config.ConfigurationProvider;

import com.google.common.collect.ImmutableList;

import java.util.List;

import org.junit.Test;

public class XmlSdkConfigurationProviderLifecycleTestCase {


  @Test
  public void validateLifecycleMethodsAreExecuted() throws Exception {
    final String name = "provider";
    final ExtensionModel extensionModel = mock(ExtensionModel.class);
    final ConfigurationModel configurationModel = mock(ConfigurationModel.class);

    final Injector injector = mock(Injector.class);
    final MuleContext muleContext = mock(MuleContext.class);
    final MuleConfiguration muleConfiguration = mock(MuleConfiguration.class);
    when(muleConfiguration.isLazyInit()).thenReturn(true);
    when(muleContext.getInjector()).thenReturn(injector);
    when(muleContext.getConfiguration()).thenReturn(muleConfiguration);

    ConfigurationProvider innerProvider = mock(ConfigurationProvider.class, withSettings().extraInterfaces(Lifecycle.class));

    Object innerLifecycleAwareObject = mock(Object.class, withSettings().extraInterfaces(Lifecycle.class));

    List<ConfigurationProvider> innerConfigProviders = ImmutableList.of(innerProvider);
    List<Object> innerConfigObjects = ImmutableList.of(innerLifecycleAwareObject);

    XmlSdkConfigurationProvider provider =
        new XmlSdkConfigurationProvider(name, innerConfigProviders, innerConfigObjects, emptyMap(), extensionModel, configurationModel, muleContext);

    initialiseIfNeeded(provider);
    startIfNeeded(provider);
    stopIfNeeded(provider);
    disposeIfNeeded(provider, null);

    verify((Initialisable) innerProvider, times(1)).initialise();
    verify((Startable) innerProvider, times(1)).start();
    verify((Stoppable) innerProvider, times(1)).stop();
    verify((Disposable) innerProvider, times(1)).dispose();

    verify((Initialisable) innerLifecycleAwareObject, times(1)).initialise();
    verify((Startable) innerLifecycleAwareObject, times(1)).start();
    verify((Stoppable) innerLifecycleAwareObject, times(1)).stop();
    verify((Disposable) innerLifecycleAwareObject, times(1)).dispose();
  }
}
