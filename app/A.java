<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".Transaction">

    <!-- RecyclerView to display the list of CardViews -->
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/transactionRecyclerView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_margin="8dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toTopOf="@id/bottom_nav"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <!-- Bottom Navigation Bar -->
    <LinearLayout
        android:id="@+id/bottom_nav"
        android:layout_width="0dp"
        android:layout_height="60dp"
        android:background="@color/white"
        android:orientation="horizontal"
        android:weightSum="4"
        android:elevation="8dp"
        android:padding="8dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent">

        <ImageButton
            android:id="@+id/btnTransaction"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:backgroundTint="@color/white"
            android:scaleType="centerInside"
            android:contentDescription="Transaction"
            android:src="@drawable/transaction_icon" />

        <ImageButton
            android:id="@+id/btnDashboard"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:backgroundTint="@color/white"
            android:scaleType="centerInside"
            android:contentDescription="Dashboard"
            android:src="@drawable/dashboard_icon" />

        <ImageButton
            android:id="@+id/btnBudget"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:backgroundTint="@color/white"
            android:scaleType="centerInside"
            android:contentDescription="Budget"
            android:src="@drawable/budget_icon" />

        <ImageButton
            android:id="@+id/btnSetting"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:backgroundTint="@color/white"
            android:scaleType="centerInside"
            android:contentDescription="Setting"
            android:src="@drawable/setting_icon" />
    </LinearLayout>

</androidx.constraintlayout.widget.ConstraintLayout>
