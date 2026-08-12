package com.prexlauncher.anim.animations

import com.prexlauncher.anim.animations.bounce.BounceEnlargeAnimator
import com.prexlauncher.anim.animations.bounce.BounceInDownAnimator
import com.prexlauncher.anim.animations.bounce.BounceInLeftAnimator
import com.prexlauncher.anim.animations.bounce.BounceInRightAnimator
import com.prexlauncher.anim.animations.bounce.BounceInUpAnimator
import com.prexlauncher.anim.animations.bounce.BounceShrinkAnimator
import com.prexlauncher.anim.animations.fade.FadeInAnimator
import com.prexlauncher.anim.animations.fade.FadeInDownAnimator
import com.prexlauncher.anim.animations.fade.FadeInLeftAnimator
import com.prexlauncher.anim.animations.fade.FadeInRightAnimator
import com.prexlauncher.anim.animations.fade.FadeInUpAnimator
import com.prexlauncher.anim.animations.fade.FadeOutAnimator
import com.prexlauncher.anim.animations.fade.FadeOutDownAnimator
import com.prexlauncher.anim.animations.fade.FadeOutLeftAnimator
import com.prexlauncher.anim.animations.fade.FadeOutRightAnimator
import com.prexlauncher.anim.animations.fade.FadeOutUpAnimator
import com.prexlauncher.anim.animations.other.PulseAnimator
import com.prexlauncher.anim.animations.other.ShakeAnimator
import com.prexlauncher.anim.animations.other.WobbleAnimator
import com.prexlauncher.anim.animations.slide.SlideInDownAnimator
import com.prexlauncher.anim.animations.slide.SlideInLeftAnimator
import com.prexlauncher.anim.animations.slide.SlideInRightAnimator
import com.prexlauncher.anim.animations.slide.SlideInUpAnimator
import com.prexlauncher.anim.animations.slide.SlideOutDownAnimator
import com.prexlauncher.anim.animations.slide.SlideOutLeftAnimator
import com.prexlauncher.anim.animations.slide.SlideOutRightAnimator
import com.prexlauncher.anim.animations.slide.SlideOutUpAnimator

enum class Animations(val animator: BaseAnimator) {
    //Bounce
    BounceInDown(BounceInDownAnimator()),
    BounceInLeft(BounceInLeftAnimator()),
    BounceInRight(BounceInRightAnimator()),
    BounceInUp(BounceInUpAnimator()),
    BounceEnlarge(BounceEnlargeAnimator()),
    BounceShrink(BounceShrinkAnimator()),

    //Fade in
    FadeIn(FadeInAnimator()),
    FadeInLeft(FadeInLeftAnimator()),
    FadeInRight(FadeInRightAnimator()),
    FadeInUp(FadeInUpAnimator()),
    FadeInDown(FadeInDownAnimator()),

    //Fade out
    FadeOut(FadeOutAnimator()),
    FadeOutLeft(FadeOutLeftAnimator()),
    FadeOutRight(FadeOutRightAnimator()),
    FadeOutUp(FadeOutUpAnimator()),
    FadeOutDown(FadeOutDownAnimator()),

    //Slide in
    SlideInLeft(SlideInLeftAnimator()),
    SlideInRight(SlideInRightAnimator()),
    SlideInUp(SlideInUpAnimator()),
    SlideInDown(SlideInDownAnimator()),

    //Slide out
    SlideOutLeft(SlideOutLeftAnimator()),
    SlideOutRight(SlideOutRightAnimator()),
    SlideOutUp(SlideOutUpAnimator()),
    SlideOutDown(SlideOutDownAnimator()),

    //Other
    Pulse(PulseAnimator()),
    Wobble(WobbleAnimator()),
    Shake(ShakeAnimator())
}