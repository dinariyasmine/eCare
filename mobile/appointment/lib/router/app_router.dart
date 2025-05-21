import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

final GoRouter appRouter = GoRouter(
  initialLocation: '/',
  routes: [
    GoRoute(
      path: '/',
      builder: (context, state) => const HomeScreen(),
    ),
    GoRoute(
      path: '/appointments',
      builder: (context, state) => const AppointmentsScreen(),
    ),
    GoRoute(
      path: '/appointment/:id',
      builder: (context, state) => AppointmentDetailsScreen(
        appointmentId: state.pathParameters['id']!,
      ),
    ),
    GoRoute(
      path: '/profile',
      builder: (context, state) => const ProfileScreen(),
    ),
  ],
  errorBuilder: (context, state) => const ErrorScreen(),
); 