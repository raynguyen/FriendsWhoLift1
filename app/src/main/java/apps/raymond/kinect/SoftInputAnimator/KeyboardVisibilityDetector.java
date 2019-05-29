package apps.raymond.kinect.SoftInputAnimator;

import android.view.ViewTreeObserver;

public class KeyboardVisibilityDetector {

    static void listen(ActivityViewHolder viewHolder, KeyboardVisibilityListener listener) {
        Detector detector = new Detector(viewHolder, listener);
        viewHolder.getNonResizableLayout().getViewTreeObserver().addOnPreDrawListener(detector);
        viewHolder.onDetach(() -> viewHolder.getNonResizableLayout().getViewTreeObserver().removeOnPreDrawListener(detector));
    }

    private static final class Detector implements ViewTreeObserver.OnPreDrawListener {

        private final ActivityViewHolder viewHolder;
        private final KeyboardVisibilityListener listener;

        private int previousHeight = -1;

        Detector(ActivityViewHolder viewHolder, KeyboardVisibilityListener listener) {
            this.viewHolder = viewHolder;
            this.listener = listener;
        }

        @Override
        public boolean onPreDraw() {
            return !detect();
        }

        private boolean detect() {
            int contentHeight = viewHolder.getResizableLayout().getHeight();
            if (contentHeight == previousHeight) {
                return false;
            }

            if (previousHeight != -1) {
                int statusBarHeight = viewHolder.getResizableLayout().getTop();
                boolean isKeyboardVisible = contentHeight < viewHolder.getNonResizableLayout().getHeight() - statusBarHeight;

                listener.onKeyboardVisibilityChanged(new KeyboardVisibilityListener.KeyboardVisibilityChangedEvent(
                        isKeyboardVisible, contentHeight, previousHeight
                ));
            }

            previousHeight = contentHeight;
            return true;
        }
    }
}
