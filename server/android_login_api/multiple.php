<?php
$mysqli = new mysqli("localhost", "root", "", "android_api");

/* check connection */
if (mysqli_connect_errno()) {
    printf("Connect failed: %s\n", mysqli_connect_error());
    exit();
}

$query = "INSERT INTO place_placetype(id_place, id_placetype) VALUES (0, 1); INSERT INTO place_placetype(id_place, id_placetype) VALUES (0, 2)";
//$query.= "INSERT INTO place_placetype(id_place, id_placetype) VALUES (0, 2); ";
/* execute multi query */
if ($mysqli->multi_query($query)) {
    do {
        /* store first result set */
        if ($result = $mysqli->store_result()) {
            while ($row = $result->fetch_row()) {
                printf("%s\n", $row[0]);
            }
            $result->free();
        }
        /* print divider */
        if ($mysqli->more_results()) {
            printf("-----------------\n");
        }
    } while ($mysqli->next_result());
}

/* close connection */
$mysqli->close();
?>