/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.perfectkey.keyboard.latin.utils;

import android.graphics.Path;

/**
 * continuous ("squircle") corners: the curvature changes gradually, so the curve starts
 * further along the edge than a circular arc with the same radius.
 * The construction follows the Figma corner smoothing: cubic, circular arc, cubic.
 */
public final class SquircleUtils {
    private static final int STEPS = 40;

    private SquircleUtils() { }

    /** Shape with squircle top-left and top-right corners and a flat bottom (to be clipped by the screen). */
    public static Path topRoundedPath(final float width, final float height, final float radius, final float smoothing) {
        final double p = (1 + smoothing) * radius;
        final double beta = Math.toRadians(90 * (1 - smoothing));
        final double theta = (Math.PI / 2 - beta) / 2;
        final double p3p4 = radius * Math.tan(theta / 2);
        final double arcLength = Math.sin(beta / 2) * radius * Math.sqrt(2);
        final double c = p3p4 * Math.cos(theta);
        final double d = c * Math.tan(theta);
        final double b = (p - arcLength - c - d) / 3;
        final double a = 2 * b;

        // top-right corner in local coordinates, starting at the end of the top edge
        final double[][] pts = new double[STEPS * 3 + 3][];
        int n = 0;
        final double[] s0 = {0, 0};
        final double[] e1 = {a + b + c, d};
        for (int i = 0; i <= STEPS; i++)
            pts[n++] = cubic(s0, new double[]{a, 0}, new double[]{a + b, 0}, e1, (double) i / STEPS);
        final double th0 = -Math.PI / 2 + theta;
        final double cx = e1[0] - radius * Math.cos(th0);
        final double cy = e1[1] - radius * Math.sin(th0);
        for (int i = 1; i <= STEPS; i++) {
            final double t = th0 + beta * i / STEPS;
            pts[n++] = new double[]{cx + radius * Math.cos(t), cy + radius * Math.sin(t)};
        }
        final double[] e2 = pts[n - 1];
        final double[] e3 = {e2[0] + d, e2[1] + a + b + c};
        for (int i = 1; i <= STEPS; i++)
            pts[n++] = cubic(e2, new double[]{e2[0] + d, e2[1] + c}, new double[]{e2[0] + d, e2[1] + b + c}, e3, (double) i / STEPS);

        final Path path = new Path();
        path.moveTo(0, height);
        // top-left corner: mirrored right corner, walked backwards
        for (int i = n - 1; i >= 0; i--)
            path.lineTo((float) (p - pts[i][0] - 0), (float) pts[i][1]);
        // top-right corner
        for (int i = 0; i < n; i++)
            path.lineTo((float) (width - p + pts[i][0]), (float) pts[i][1]);
        path.lineTo(width, height);
        path.close();
        return path;
    }

    private static double[] cubic(final double[] p0, final double[] p1, final double[] p2, final double[] p3, final double t) {
        final double u = 1 - t;
        return new double[]{
                u * u * u * p0[0] + 3 * u * u * t * p1[0] + 3 * u * t * t * p2[0] + t * t * t * p3[0],
                u * u * u * p0[1] + 3 * u * u * t * p1[1] + 3 * u * t * t * p2[1] + t * t * t * p3[1]};
    }
}
