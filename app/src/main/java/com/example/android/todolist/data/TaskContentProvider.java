/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.todolist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.android.todolist.data.TaskContract.TaskEntry;
// TODO (1) Verify that TaskContentProvider extends from ContentProvider and implements required methods
public class TaskContentProvider extends ContentProvider {
    // TODO (1) Define final integer constants for the directory of tasks and a single item.
    // It's convention to use 100, 200, 300, etc for directories,
    // and related ints (101, 102, ..) for items in that directory
    private static final int TASKS_ID=100;
    private static final int TASK_ID=101;
    // TODO (3) Declare a static variable for the Uri matcher that you construct
     private static final UriMatcher uriMatcher=buildUriMatcher();

    // TODO (2) Define a static buildUriMatcher method that associates URI's with their int match
   public static UriMatcher buildUriMatcher()
   {
    UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
   uriMatcher.addURI(TaskContract.TASK_CONTENT_AUTHORITY_URI,TaskContract.TASK_PATH,TASKS_ID);
   uriMatcher.addURI(TaskContract.TASK_CONTENT_AUTHORITY_URI,TaskContract.TASK_PATH+"/#",TASK_ID);
  return uriMatcher;
   }
    private TaskDbHelper taskDbHelper;
    /* onCreate() is where you should initialize anything you’ll need to setup
    your underlying data source.
    In this case, you’re working with a SQLite database, so you’ll need to
    initialize a DbHelper to gain access to it.
     */
    @Override
    public boolean onCreate() {

        // [Hint] Declare the DbHelper as a global variable
        taskDbHelper=new TaskDbHelper(getContext());
        return true;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO (1) Get access to the task database (to write new data to)
        SQLiteDatabase mDb=taskDbHelper.getWritableDatabase();
        Uri returnUri;
        // TODO (2) Write URI matching code to identify the match for the tasks directory

        switch (uriMatcher.match(uri))
        {
            case TASKS_ID:
                long id=mDb.insert(TaskEntry.TABLE_NAME,null,values);
                if(id>0)
                {
                    returnUri= ContentUris.withAppendedId(TaskEntry.TASK_CONTENT_URI,id);
                }
                else
                {
                    throw new android.database.SQLException("Data hasn't been inserted");
                }
                break;
            default:
                throw new UnsupportedOperationException("Given Uri Is Devil URi");

        }
        // TODO (3) Insert new values into the database
        // TODO (4) Set the value for the returnedUri and write the default case for unknown URI's

        // TODO (5) Notify the resolver if the uri has been changed, and return the newly inserted U
      getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
     final SQLiteDatabase mDb=taskDbHelper.getReadableDatabase();
     Cursor cursor;
     int match=uriMatcher.match(uri);
     switch (match)
     {
         case TASKS_ID:
             cursor=mDb.query(TaskEntry.TABLE_NAME,null,selection,selectionArgs,null,null,sortOrder);
             break;
         default:
             throw new android.database.SQLException("Data Can't Be Accessed");
     }
    cursor.setNotificationUri(getContext().getContentResolver(),uri);
     return cursor;

    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase mDb=taskDbHelper.getReadableDatabase();
        int deletedRows=0;
        int match=uriMatcher.match(uri);
        switch (match)
        {
            case TASKS_ID:

                deletedRows=mDb.delete(TaskEntry.TABLE_NAME,"_id=?",new String[]{uri.getPathSegments().get(1)});
                break;
            default:
                throw new android.database.SQLException("Data Can't Be Deleted");
        }
        if(deletedRows>0)
        {
       getContext().getContentResolver().notifyChange(uri,null);
        }

        return deletedRows;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}
