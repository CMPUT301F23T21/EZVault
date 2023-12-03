package com.example.ezvault;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.not;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.ezvault.model.ItemList;
import com.example.ezvault.model.Tag;
import com.example.ezvault.model.User;
import com.example.ezvault.utils.UserManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@RunWith(AndroidJUnit4.class)
@HiltAndroidTest
@LargeTest
public class TagListTest {
    HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    ActivityScenarioRule<MainActivity> scenarioRule
            = new ActivityScenarioRule<>(MainActivity.class);
    @Rule
    public RuleChain chain = RuleChain
            .outerRule(hiltRule)
            .around(scenarioRule);

    private static boolean hasEmulatorSet = false;

    @Before
    public void setup() {
        hiltRule.inject();
        if (hasEmulatorSet) return;
        hasEmulatorSet = true;
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

    private void prelude(Consumer<User> consumer) {
        final NavController[] navController = new NavController[1];
        scenarioRule.getScenario().onActivity(activity -> {
            navController[0] = Navigation.findNavController(activity, R.id.navHostFragment);
            User user = mockUser();
            consumer.accept(user);
            userManager.setUser(user);
            navController[0].navigate(R.id.tagsFragment);
        });
    }

    @Test
    public void emptyTagList() {
        prelude(x -> {});
        onView(withId(R.id.empty_tags))
                .check(matches(isDisplayed()));
    }

    @Test
    public void nonEmptyTagList() {
        prelude(u -> u.getItemList().getTags().add(new Tag("test", null)));
        onView(withId(R.id.empty_tags))
                .check(matches(not(isDisplayed())));
    }

    @Test
    public void addedTagIsDisplayed() {
        prelude(x -> {});
        onView(withId(R.id.empty_tags))
                .check(matches(isDisplayed()));
        onView(withId(R.id.button_add_tag))
                .perform(click());
        onView(withId(R.id.tag_dialog_edit_text))
                .perform(click())
                .perform(typeText("tag1"), closeSoftKeyboard());
        onView(withId(R.id.tag_dialog_add_button))
                .perform(click());

        onView(withText("tag1"))
                .check(matches(isDisplayed()));
    }
}
