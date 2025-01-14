package com.amplitude.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
@Config(manifest=Config.NONE)
public class AmplitudeServerZoneTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { AmplitudeServerZone.US, Constants.EVENT_LOG_URL, Constants.DYNAMIC_CONFIG_URL },
            { null, Constants.EVENT_LOG_URL, Constants.DYNAMIC_CONFIG_URL },
            { AmplitudeServerZone.EU, Constants.EVENT_LOG_EU_URL, Constants.DYNAMIC_CONFIG_EU_URL }
        });
    }

    private AmplitudeServerZone serverZone;
    private String expectedEventLogUrl;
    private String expectedDynamicConfigUrl;

    public AmplitudeServerZoneTest(
        AmplitudeServerZone serverZone,
        String expectedEventLogUrl,
        String expectedDynamicConfigUrl
    ) {
        this.serverZone = serverZone;
        this.expectedEventLogUrl = expectedEventLogUrl;
        this.expectedDynamicConfigUrl = expectedDynamicConfigUrl;
    }

    @Test
    public void testGetCorrectUrlForAmplitudeServerZone() {
        assertEquals(expectedEventLogUrl, AmplitudeServerZone.getEventLogApiForZone(serverZone));
        assertEquals(expectedDynamicConfigUrl, AmplitudeServerZone.getDynamicConfigApi(serverZone));
    }
}
