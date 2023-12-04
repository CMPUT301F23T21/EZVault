/**
 * Intent testing relating to user story 01.01.01
 * and user story 01.02.01
 */

package com.example.ezvault;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.ezvault.model.Item;
import com.example.ezvault.model.ItemBuilder;
import com.example.ezvault.model.ItemList;
import com.example.ezvault.model.User;
import com.example.ezvault.utils.UserManager;
import com.example.ezvault.viewmodel.MainActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@RunWith(AndroidJUnit4.class)
@HiltAndroidTest
@LargeTest
public class ItemsTest {
    HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    ActivityScenarioRule<MainActivity> scenarioRule
            = new ActivityScenarioRule<>(MainActivity.class);
    @Rule
    public RuleChain chain = RuleChain
            .outerRule(hiltRule)
            .around(scenarioRule);

    private static boolean setEmulator = false;

    @Before
    public void setup() {
        hiltRule.inject();

        if (setEmulator) return;
        setEmulator = true;
        FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080);
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
        FirebaseStorage.getInstance().useEmulator("10.0.2.2", 9199);
    }

    @Inject
    UserManager userManager;

    private User mockUser() {
        ItemList itemList = new ItemList();
        return new User("test", "INVALID", itemList);
    }

    /**
     * This function sets up a user with an item.
     */
    @Before
    public void prelude() {
        final NavController[] navController = new NavController[1];
        scenarioRule.getScenario().onActivity(activity -> {
            navController[0] = Navigation.findNavController(activity, R.id.navHostFragment);
            User user = mockUser();
            ItemList itemList = user.getItemList();
            Item item = new ItemBuilder()
                    .setMake("Pringles")
                    .setModel("Original")
                    .setDescription("Chips")
                    .setComment("156 g")
                    .setValue(2.99)
                    .setCount(1.5)
                    .setSerialNumber("88899608")
                    .setAcquisitionDate(new Timestamp(new Date()))
                    .build();
            itemList.add(item);
            userManager.setUser(user);
            navController[0].navigate(R.id.itemsFragment);
        });
    }

    /**
     * This test ensures that a user's item's are displayed.
     */
    @Test
    public void haveItemsTest() {
        // assure make is shown
        onView(withSubstring("Pringles"))
                .check(matches(isDisplayed()));

        //assure model is shown
        onView(withSubstring("Original"))
                .check(matches(isDisplayed()));

        //add new item
        onView(withId(R.id.button_add_item))
                .perform(click());

        // set make of new item
        onView(withId(R.id.edit_details_make))
                .perform(click())
                .perform(typeText("Pringles"))
                .perform(closeSoftKeyboard());

        // set model of new item
        onView(withId(R.id.edit_details_model))
                .perform(click())
                .perform(typeText("Ketchup"))
                .perform(closeSoftKeyboard());

        // set description of item
        onView(withId(R.id.edit_details_description))
                .perform(click())
                .perform(typeText("156 grams"))
                .perform(closeSoftKeyboard());

        // set user's comment on item
        onView(withId(R.id.edit_details_comment))
                .perform(click())
                .perform(typeText("ketchup flavor"))
                .perform(closeSoftKeyboard());

        // set count of item
        onView(withId(R.id.edit_details_count))
                .perform(click())
                .perform(typeText("1"))
                .perform(closeSoftKeyboard());

        // set value of item
        onView(withId(R.id.edit_details_value))
                .perform(click())
                .perform(typeText("1.99"))
                .perform(closeSoftKeyboard());

        // set serial number
        onView(withId(R.id.edit_details_serial_number))
                .perform(click())
                .perform(typeText("064100118690"))
                .perform(closeSoftKeyboard());

        // select date as today (default)
        onView(withContentDescription("pick date"))
                .perform(click());
        onView(withText("OK"))
                .perform(click());

        //add item
        onView(withId(R.id.edit_details_save))
                .perform(click());

        //ensure new item exists on list
        onView(withSubstring("Ketchup"))
                .check(matches(isDisplayed()));
    }

    /**
     * Ensure that all details are displayed for an item.
     * Ensure that they are correct.
     * For user story 01.01.01 and 01.02.01
     */
    @Test
    public void itemDetails() {
        // view details of item
        onView(withSubstring("Original"))
                .perform(click());

        // ensure make is properly set
        onView(withId(R.id.edit_details_make))
                .check(matches(withText("Pringles")));

        // ensure model is properly set
        onView(withId(R.id.edit_details_model))
                .check(matches(withText("Original")));

        // ensure description is properly set
        onView(withId(R.id.edit_details_description))
                .check(matches(withText("Chips")));

        // ensure value is properly set
        onView(withId(R.id.edit_details_value))
                .check(matches(withText("2.99")));

        // ensure count is properly set
        onView(withId(R.id.edit_details_count))
                .check(matches(withText("1.5")));

        // ensure serial number is properly set
        onView(withId(R.id.edit_details_serial_number))
                .check(matches(withText("88899608")));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        onView(withId(R.id.edit_details_date))
                .check(matches(withText(format.format(new Date()))));
    }
}