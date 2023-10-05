const functions = require("firebase-functions");
const admin = require("firebase-admin");
const googleapis = require('googleapis');
const { JWT } = require('google-auth-library');
const serviceAccount = require('./wordefull-cf277-e8d47b1d8be6.json');

admin.initializeApp();
// TODO: Replace the following with your app's Firebase project configuration
exports.verifyPurchases = functions.https.onRequest(async (request, response) => {
    try {
        const purchaseToken = request.query.purchaseToken;
        const productId = request.query.productId;
        const productType = request.query.productType;
        let isValid = false;
        let packageName = "com.gamovation.tilecl"

        const getAuthorizedClient = () => new JWT({
            email: serviceAccount.client_email,
            key: serviceAccount.private_key,
            scopes: ['https://www.googleapis.com/auth/androidpublisher']
        });

       const playApi = googleapis.google.androidpublisher({
           version: 'v3',
           auth: getAuthorizedClient()
       });


        let checkPurchaseResult;

        if (productType === 'inapp') {
            // For one-time products
            checkPurchaseResult = await playApi.purchases.products.get({
                packageName: packageName,
                productId: productId,
                token: purchaseToken
            });
        } else if (productType === 'subs') {
            // For subscriptions
            checkPurchaseResult = await playApi.purchases.subscriptions.get({
                packageName: packageName,
                subscriptionId: productId, // Note: subscriptionId is used for subscriptions
                token: purchaseToken
            });
        } else {
            throw new Error("Invalid productType provided");
        }


        // Проверка результата запроса
        if (checkPurchaseResult.status === 200) {
            const purchaseState = checkPurchaseResult.data.purchaseState;
            isValid = purchaseState === 0;
        }

        response.send({ isValid });
    } catch (error) {
        console.error("Error verifying purchase:", error);
        response.status(500).send({ error: "Error verifying purchase" });
    }
});
