package com.example.ezvault;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
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

import org.hamcrest.Matcher;
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
public class DeleteItemTest {
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
            Item firstItem = new ItemBuilder()
                    .setMake("Make1")
                    .setModel("Model1")
                    .setDescription("Description for Item 1")
                    .setCount(10.0)
                    .setAcquisitionDate(new Timestamp(new Date()))
                    .setComment("Comment for Item 1")
                    .setValue(1000.0)
                    .setSerialNumber("SN1001")
                    .build();
            Item secondItem = new ItemBuilder()
                    .setMake("Make2")
                    .setModel("Model2")
                    .setDescription("Description for Item 2")
                    .setCount(20.0)
                    .setAcquisitionDate(new Timestamp(new Date()))
                    .setComment("Comment for Item 2")
                    .setValue(2000.0)
                    .setSerialNumber("SN2002")
                    .build();
            Item thirdItem = new ItemBuilder()
                    .setMake("Make3")
                    .setModel("Model3")
                    .setDescription("Description for Item 3")
                    .setCount(30.0)
                    .setAcquisitionDate(new Timestamp(new Date()))
                    .setComment("Comment for Item 3")
                    .setValue(3000.0)
                    .setSerialNumber("SN3003")
                    .build();
            user.getItemList().add(firstItem);
            user.getItemList().add(secondItem);
            user.getItemList().add(thirdItem);
            userManager.setUser(user);
            navController[0].navigate(R.id.itemsFragment);
        });
    }

    private static ViewAction checkBox() {
        return new ViewAction() {
            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(R.id.item_checkbox);
                v.performClick();
            }
        };
    }

    @Test
    public void deleteAllItems() {
        // enter edit mode
        onView(withId(R.id.toolbar_trash))
                .perform(click());

        // check all boxes
        for (int i = 0; i < 3; i++) {
            onView(withId(R.id.recyclerView))
                    .perform(scrollToPosition(i))
                    .perform(actionOnItemAtPosition(i, checkBox()));
        }

        // confirm deletion
        onView(withId(R.id.edit_item_confirm))
                .perform(click());
        onView(withText("Confirm"))
                .perform(click());
    }
}
