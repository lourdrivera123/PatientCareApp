package com.beta.zem.patientcareapp.adapter;

/**
 * Created by User PC on 12/2/2015.
 */

public interface IconPagerAdapter {
    /**
     * Get icon representing the page at {@code index} in the adapter.
     */
    int getIconResId(int index);

    // From PagerAdapter
    int getCount();
}
