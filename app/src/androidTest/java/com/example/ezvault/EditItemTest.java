/*
 * Files Purpose: Testing Editing Items
 */

package com.example.ezvault;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;

import static org.hamcrest.CoreMatchers.not;

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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import java.util.Date;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@RunWith(AndroidJUnit4.class)
@HiltAndroidTest
@LargeTest
public class EditItemTest {
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
    @Test
    public void editTest() {
        prelude();

        // ensure that the initial displayed total value is correct
        onView(withId(R.id.text_total_value))
                .check(matches(withSubstring(String.format("%.2f", 2.99*1.5))));

        // enter into editing the items
        onView(withSubstring("Pringles"))
                .perform(click());

        onView(withId(R.id.edit_details_count))
                .perform(click())
                .perform(replaceText("1.0"))
                .perform(closeSoftKeyboard());

        // save the edits
        onView(withId(R.id.edit_details_save))
                .perform(scrollTo())
                .perform(click());

        pressBack();

        // ensure item view displays updated quantity
        onView(withId(R.id.quantity_text))
                .check(matches(withSubstring("1.0")));

        // ensure that the new total value has changed
        onView(withId(R.id.text_total_value))
                .check(matches(withSubstring("2.99")));
    }

    @Test
    public void cancelEdit() {
        prelude();

        // ensure that the initial displayed total value is correct
        onView(withId(R.id.text_total_value))
                .check(matches(withSubstring(String.format("%.2f", 2.99*1.5))));

        // enter into editing the items
        onView(withSubstring("Pringles"))
                .perform(click());

        onView(withId(R.id.edit_details_count))
                .perform(click())
                .perform(replaceText("1.0"))
                .perform(closeSoftKeyboard());

        pressBack();

        // ensure item view does NOT display updated quantity
        onView(withId(R.id.quantity_text))
                .check(matches(not(withSubstring("1.0"))));

        // ensure that the new total value has NOT changed
        onView(withId(R.id.text_total_value))
                .check(matches(not(withSubstring("2.99"))));
        onView(withId(R.id.text_total_value))
                .check(matches(withSubstring(String.format("%.2f", 2.99*1.5))));
    }
}
