<#assign title="Sign in Help">
<#assign style="substyle.css">

<!DOCTYPE html>
<html lang="en" class="${userCSS}">
<head>
    <#include "inc/_head.ftl">
</head>

<body>

<div id="navbar" class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">GaeShiro</a>
        </div>
        <div id="navbar-collapse" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li class="active"><a href="index.html">Home</a></li>
            </ul>
            <#include "inc/loginoutbutton.ftl">
        </div>
    </div>
</div>

<div class="container">

    <div class="content">
        <section>
            <div class="page-header">
                <h1>Signing in</h1>
            </div>
            <div class = "row">
                <div class="span4"  style="min-height: 0;">
                    &nbsp;  
                </div>
               <div class="span6">
                   <h3>Email, Google or Facebook</h3>
                   <p>You can choose whether to login with an email address, in which case you have to
                      provide your Email address and a password, or use an account
                      with Google or Facebook.</p>
                   <p>If you use Google or Facebook we ask only for your email address.  We need this to contact you if there
                      are problems and we need to contact you.</p>
                   <p>You can log in with any method as long as you use the same Email address. So you can log in with Email one
                      day and Google the next if you wish.  Your id is your email address.</p>
               </div>
            </div>
        </section>
    </div>


</div>

<#include "inc/footer.ftl">
<#include "inc/_foot.ftl">

</body>
</html>
