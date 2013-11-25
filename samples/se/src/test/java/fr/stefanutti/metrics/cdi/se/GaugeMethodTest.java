/**
 * Copyright (C) 2013 Antonin Stefanutti (antonin.stefanutti@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.stefanutti.metrics.cdi.se;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import fr.stefanutti.metrics.cdi.MetricsExtension;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(Arquillian.class)
public class GaugeMethodTest {

    private final static String GAUGE_NAME = MetricRegistry.name(GaugeMethodBean.class, "gaugeMethod");

    @Deployment
    static Archive<?> createTestArchive() {
        return ShrinkWrap.create(JavaArchive.class)
            // Test bean
            .addClass(GaugeMethodBean.class)
            // Metrics CDI extension
            .addPackages(false, MetricsExtension.class.getPackage())
            // Bean archive deployment descriptor
            .addAsManifestResource("beans.xml");
    }

    @Inject
    private MetricRegistry registry;

    @Inject
    private GaugeMethodBean bean;

    @Test
    @InSequence(1)
    public void gaugeCalledWithDefaultValue() {
        assertThat("Gauge is not registered correctly", registry.getGauges(), hasKey(GAUGE_NAME));
        @SuppressWarnings("unchecked")
        Gauge<Long> gauge = registry.getGauges().get(GAUGE_NAME);

        // Make sure that the gauge has the expected value
        assertThat("Gauge value is incorrect", gauge.getValue(), is(equalTo(0L)));
    }

    @Test
    @InSequence(2)
    public void callGaugeAfterSetterCall() {
        assertThat("Gauge is not registered correctly", registry.getGauges(), hasKey(GAUGE_NAME));
        @SuppressWarnings("unchecked")
        Gauge<Long> gauge = registry.getGauges().get(GAUGE_NAME);

        // Call the setter method and assert the gauge is up-to-date
        long value = Math.round(Math.random() * Long.MAX_VALUE);
        bean.setGauge(value);
        assertThat("Gauge value is incorrect", gauge.getValue(), is(equalTo(value)));
    }
}