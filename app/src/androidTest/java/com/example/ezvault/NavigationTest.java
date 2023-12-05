package com.example.ezvault;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.Manifest;
import android.os.SystemClock;

import androidx.annotation.OptIn;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.base.MainThread;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.ezvault.data.UserManager;
import com.example.ezvault.model.ItemList;
import com.example.ezvault.model.User;
import com.example.ezvault.view.MainActivity;
import com.example.ezvault.view.fragment.WelcomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@RunWith(AndroidJUnit4.class)
@HiltAndroidTest
@LargeTest
public class NavigationTest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainActivity> scenarioRule
            = new ActivityScenarioRule<>(MainActivity.class);

    @Rule(order = 2)
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Before
    public void setup() {
        hiltRule.inject();
    }

    @Test
    public void testNavigation() {
        // if welcome page is displayed
        onView(withId(R.id.new_user_button)).check(matches(isDisplayed()));

        // if navigates to create user page
        onView(withId(R.id.new_user_button)).perform(click());
        onView(withId(R.id.create_user_button)).check(matches(isDisplayed()));

        // if navigates back to welcome page
        onView(withId(R.id.create_user_back_button)).perform(click());
        onView(withId(R.id.existing_user_button)).check(matches(isDisplayed()));

        // if navigates back to welcome page
        onView(withId(R.id.existing_user_button)).perform(click());
        onView(withId(R.id.login_button)).check(matches(isDisplayed()));

        // login
        onView(withId(R.id.username_text)).perform(typeText("test@gmail.com"))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.password_text)).perform(typeText("test123"))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        // if items fragment is shown
        SystemClock.sleep(5000);
        onView(ViewMatchers.withId(R.id.itemsFragment)).check(matches(isDisplayed()));

        // navigate to tags
        onView(withId(R.id.tagsFragment)).perform(click());
        onView(withId(R.id.tagsFragment)).check(matches(isDisplayed()));

        // navigate to profile
        onView(withId(R.id.profileFragment)).perform(click());
        onView(withId(R.id.profileFragment)).check(matches(isDisplayed()));

        // navigate to items
        onView(withId(R.id.itemsFragment)).perform(click());
        onView(withId(R.id.text_filterSort)).check(matches(isDisplayed()));

        // navigate to add item
        onView(withId(R.id.button_add_item)).perform(click());
        onView(withId(R.id.edit_details_make)).check(matches(isDisplayed()));

        // close add item
        onView(withContentDescription("Navigate up")).perform(click());
        onView(withId(R.id.text_filterSort)).check(matches(isDisplayed()));

        // navigate to filter fragment
        onView(withId(R.id.text_filterSort)).perform(click());
        onView(withId(R.id.text_filter)).check(matches(isDisplayed()));

    }
}
