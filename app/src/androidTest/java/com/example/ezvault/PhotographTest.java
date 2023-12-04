package com.example.ezvault;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.RootMatchers.isTouchable;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.*;

import static org.hamcrest.CoreMatchers.not;

import android.Manifest;

import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

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
import kotlin.jvm.JvmField;

@RunWith(AndroidJUnit4.class)
@HiltAndroidTest
@LargeTest
public class PhotographTest {
    HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    ActivityScenarioRule<MainActivity> scenarioRule
            = new ActivityScenarioRule<>(MainActivity.class);


    public GrantPermissionRule cameraRuntimePermission = GrantPermissionRule
            .grant(Manifest.permission.CAMERA);

    @Rule
    public RuleChain chain = RuleChain
            .outerRule(hiltRule)
            .around(cameraRuntimePermission)
            .around(scenarioRule);


    @Before
    public void setup() {
        hiltRule.inject();
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
                    .setMake("Dinosaur")
                    .setModel("Crackers")
                    .setDescription("Tasty crackers")
                    .setComment("156 g")
                    .setValue(2.99)
                    .setCount(94)
                    .setSerialNumber("88899608")
                    .setAcquisitionDate(new Timestamp(new Date()))
                    .build();
            itemList.add(item);
            userManager.setUser(user);
            navController[0].navigate(R.id.itemsFragment);
        });
    }

    /**
     * Ensures that a user is able to take a photo with the camera
     * Ensures that the photo can be added to the item
     * For user story 04.01.01
     * @throws InterruptedException
     */
    @Test
    public void takePhoto() throws InterruptedException {

        // Click the item
        onView(withId(R.id.view_details_image)).perform(click());

        // Click the edit button
        onView(withId(R.id.view_item_edit)).perform(click());


        // Click yes to edit
        onView(withText("Yes"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        // Click button to switch to camera
        onView(withId(R.id.edit_take_pic)).perform(click());

        // Wait for camera to load
        Thread.sleep(2000);

        // Take photo
        onView(withId(R.id.image_capture_button)).perform(click());

        // Wait for image to save to disk
        Thread.sleep(200);

        // Press back button
        Espresso.pressBack();

        // See if image was added
        onView(withId(R.id.imageview_photo)).check(matches(not(doesNotExist())));
    }


    /**
     * Ensures that a user is able to take a photo with the camera
     * Ensures that the photo can be added to the item
     * Ensures that the photo is deleted from the ite,
     * For user story 04.03.01
     * @throws InterruptedException
     */
    @Test
    public void removePhoto() throws InterruptedException {

        // Click the item
        onView(withId(R.id.view_details_image)).perform(click());

        // Click the edit button
        onView(withId(R.id.view_item_edit)).perform(click());


        // Click yes to edit
        onView(withText("Yes"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        // Click button to switch to camera
        onView(withId(R.id.edit_take_pic)).perform(click());

        // Wait for camera to load
        Thread.sleep(2000);

        // Take photo
        onView(withId(R.id.image_capture_button)).perform(click());

        // Wait for image to save to disk
        Thread.sleep(200);

        // Press back button
        Espresso.pressBack();

        // See if image was added
        onView(withId(R.id.imageview_photo)).check(matches(not(doesNotExist())));

        // Click remove
        onView(withId(R.id.add_item_remove_photo)).perform(click());

        // See if image was removed
        onView(withId(R.id.imageview_photo)).check(doesNotExist());

    }

    /**
     * Ensures that the user can select a photo from their gallery
     * For user story 04.02.01
     * @throws InterruptedException
     */
    @Test
    public void chooseFromGallery() throws InterruptedException {

        // Click the item
        onView(withId(R.id.view_details_image)).perform(click());

        // Click the edit button
        onView(withId(R.id.view_item_edit)).perform(click());


        // Click yes to edit
        onView(withText("Yes"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        // Click button to switch to camera
        onView(withId(R.id.edit_gallery)).perform(click());

        // Wait for gallery to load
        Thread.sleep(1000);

        // Check to see if the activity changed to the gallery picker
        // We do this by checking if our MainActivity was paused
        assert(!scenarioRule.getScenario().getState().isAtLeast(Lifecycle.State.RESUMED));
    }
}
