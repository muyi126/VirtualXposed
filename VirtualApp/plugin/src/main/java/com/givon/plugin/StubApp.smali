.class public Lcom/givon/plugin/StubApp;
.super Ljava/lang/Object;
.source "StubApp.java"


# direct methods
.method public constructor <init>()V
    .registers 1

    .prologue
    .line 14
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static getOrigApplicationContext(Landroid/content/Context;)Landroid/content/Context;
    .registers 1
    .param p0, "context"    # Landroid/content/Context;

    .prologue
    .line 16
    return-object p0
.end method

.method public static interface11(I)V
    .registers 1
    .param p0, "aa"    # I

    .prologue
    .line 21
    return-void
.end method
